package CTRLX.vision;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

public class LineFollowerPipeline extends OpenCvPipeline {
    private volatile String decision = "NONE";
    private volatile double lineError = 0;

    @Override
    public Mat processFrame(Mat input) {
        // Convert to HSV
        Mat hsv = new Mat();
        Imgproc.cvtColor(input, hsv, Imgproc.COLOR_RGB2HSV);

        // Masks
        Mat maskBlack = new Mat();
        Mat maskGreen = new Mat();

        Scalar lowerBlack = new Scalar(VisionConfig.LOWER_BLACK[0], VisionConfig.LOWER_BLACK[1], VisionConfig.LOWER_BLACK[2]);
        Scalar upperBlack = new Scalar(VisionConfig.UPPER_BLACK[0], VisionConfig.UPPER_BLACK[1], VisionConfig.UPPER_BLACK[2]);

        Scalar lowerGreen = new Scalar(VisionConfig.LOWER_GREEN[0], VisionConfig.LOWER_GREEN[1], VisionConfig.LOWER_GREEN[2]);
        Scalar upperGreen = new Scalar(VisionConfig.UPPER_GREEN[0], VisionConfig.UPPER_GREEN[1], VisionConfig.UPPER_GREEN[2]);

        Core.inRange(hsv, lowerBlack, upperBlack, maskBlack);
        Core.inRange(hsv, lowerGreen, upperGreen, maskGreen);

        // Green markers check
        List<MatOfPoint> greenContours = new ArrayList<>();
        Imgproc.findContours(maskGreen, greenContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        boolean leftMarker = false;
        boolean rightMarker = false;
        boolean leftHasBlackBelow = false;
        boolean rightHasBlackBelow = false;

        int w = input.width();
        int h = input.height();

        for (MatOfPoint contour : greenContours) {
            double area = Imgproc.contourArea(contour);
            if (area < 500) continue;

            Moments m = Imgproc.moments(contour);
            if (m.m00 == 0) continue;

            int cx = (int) (m.m10 / m.m00);
            int cy = (int) (m.m01 / m.m00);

            // Draw contour + center
            Imgproc.drawContours(input, List.of(contour), -1, new Scalar(0, 255, 0), 2);
            Imgproc.circle(input, new Point(cx, cy), 5, new Scalar(255, 0, 0), -1);

            boolean isLeft = cx < w / 2;
            if (isLeft) leftMarker = true;
            else rightMarker = true;

            // Quadrant below green
            int qx1 = Math.max(cx - 15, 0);
            int qy1 = Math.min(cy + 50, h - 1);
            int qx2 = Math.min(cx + 15, w - 1);
            int qy2 = Math.min(cy + 80, h - 1);

            Rect roi = new Rect(new Point(qx1, qy1), new Point(qx2, qy2));
            Imgproc.rectangle(input, roi, new Scalar(0, 255, 255), 2);

            if (roi.width > 0 && roi.height > 0) {
                Mat subBlack = maskBlack.submat(roi);
                int blackPixels = Core.countNonZero(subBlack);
                int totalPixels = roi.width * roi.height;
                double percentBlack = (blackPixels * 100.0) / totalPixels;

                if (percentBlack > 50) {
                    if (isLeft) leftHasBlackBelow = true;
                    else rightHasBlackBelow = true;
                }
                subBlack.release();
            }
        }

        // BLACK - only consider largest contour for error
        List<MatOfPoint> blackContours = new ArrayList<>();
        Imgproc.findContours(maskBlack.clone(), blackContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if (!blackContours.isEmpty()) {
            MatOfPoint largestBlack = blackContours.get(0);
            double maxArea = 0;
            for (MatOfPoint contour : blackContours) {
                double area = Imgproc.contourArea(contour);
                if (area > maxArea) {
                    maxArea = area;
                    largestBlack = contour;
                }
                Imgproc.drawContours(input, List.of(largestBlack), -1, new Scalar(0, 255, 0), 2);
            }

            Moments m = Imgproc.moments(largestBlack);
            if (m.m00 != 0) {
                double cx = m.m10 / m.m00; // x-center of line
                double cy = m.m01 / m.m00;
                lineError = cx - w / 2.0; // positive → line right, negative → line left
                Imgproc.circle(input, new Point(cx, cy), 5, new Scalar(255, 255, 0), -1); // cyan dot

            } else {
                lineError = 0; // fallback if contour degenerate
            }
        } else {
            lineError = 0; // no black detected
        }


        // --- DECISION TREE ---
        if (leftMarker || rightMarker) {
            // Green markers override
            if ((leftMarker && leftHasBlackBelow) || (rightMarker && rightHasBlackBelow)) {
                decision = "INTERSECTION";
            } else if (leftMarker && !leftHasBlackBelow && !rightMarker) {
                decision = "TURN LEFT";
            } else if (rightMarker && !rightHasBlackBelow && !leftMarker) {
                decision = "TURN RIGHT";
            } else if (leftMarker && rightMarker && !leftHasBlackBelow && !rightHasBlackBelow) {
                decision = "U-TURN";
            } else {
                decision = "FOLLOW LINE";
            }
        } else {
            // --- No green markers → fallback to black line decision ---
            decision = checkBlackDirection(input, maskBlack);
        }

        // Draw decision on frame
        Imgproc.putText(input, decision, new Point(30, 40),
                Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 255), 3);

        hsv.release();
        maskBlack.release();
        maskGreen.release();
        return input;
    }

    private String checkBlackDirection(Mat frame, Mat maskBlack) {
        int w = frame.width();
        int h = frame.height();
        int regionHeight = 100;
        int regionWidth = 140;

        Rect forwardROI = new Rect(
                w / 2 - regionWidth / 2, 20,
                regionWidth, regionHeight);
        Rect leftROI = new Rect(
                40, h / 2 - regionHeight / 2,
                regionWidth, regionHeight);
        Rect rightROI = new Rect(
                w - 40 - regionWidth, h / 2 - regionHeight / 2,
                regionWidth, regionHeight);

        // Draw ROIs
        Imgproc.rectangle(frame, forwardROI, new Scalar(255, 255, 255), 1);
        Imgproc.rectangle(frame, leftROI, new Scalar(255, 255, 255), 1);
        Imgproc.rectangle(frame, rightROI, new Scalar(255, 255, 255), 1);

        // Helper
        boolean forward = percentBlack(maskBlack.submat(forwardROI)) > 30;
        boolean left = percentBlack(maskBlack.submat(leftROI)) > 30;
        boolean right = percentBlack(maskBlack.submat(rightROI)) > 30;

        if (forward || (left && right) || (!forward && !left && !right)) {
            return "GO FORWARD";
        } else if (left) {
            return "GO LEFT";
        } else if (right) {
            return "GO RIGHT";
        }
        return "GO FORWARD";
    }

    private double percentBlack(Mat roi) {
        int black = Core.countNonZero(roi);
        int total = roi.width() * roi.height();
        roi.release();
        return (black * 100.0) / total;
    }

    public String getDecision() {
        return decision;
    }

    public double getLineError() {
        return lineError;
    }
}
