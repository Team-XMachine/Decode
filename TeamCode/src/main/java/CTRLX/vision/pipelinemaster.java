package CTRLX.vision;

import com.acmerobotics.dashboard.config.Config;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

@Config
public class pipelinemaster extends OpenCvPipeline {

    double leftPercent, rightPercent = 0;

    @Override
    public Mat processFrame(Mat input) {

        Mat hsv = new Mat();

        Imgproc.cvtColor(input, hsv, Imgproc.COLOR_RGB2HSV);

        Mat lineMask = new Mat();

        Scalar lowerBlack = new Scalar(VisionConfig.LOWER_BLACK[0], VisionConfig.LOWER_BLACK[1], VisionConfig.LOWER_BLACK[2]);
        Scalar upperBlack = new Scalar(VisionConfig.UPPER_BLACK[0], VisionConfig.UPPER_BLACK[1], VisionConfig.UPPER_BLACK[2]);

        Core.inRange(hsv, lowerBlack, upperBlack, lineMask);

        List<MatOfPoint> blackContours = new ArrayList<>();
        Imgproc.findContours(lineMask, blackContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        int w = lineMask.width();
        int h = lineMask.height();

        Imgproc.rectangle(input, new Point(0, 0), new Point(w/2, h), new Scalar(255, 0, 0), 2);
        Imgproc.rectangle(input, new Point(w/2, 0), new Point(w, h), new Scalar(0, 255, 0), 2);

        Mat leftMask = lineMask.submat(0, h, 0, w/2);
        Mat rightMask = lineMask.submat(0, h, w/2, w);

        double leftBlack = Core.countNonZero(leftMask);
        double rightBlack = Core.countNonZero(rightMask);
        double totalBlack = leftBlack + rightBlack;

        // Distribution of black pixels across halves
        leftPercent = totalBlack > 0 ? (leftBlack / totalBlack) * 100.0 : 0;
        rightPercent = totalBlack > 0 ? (rightBlack / totalBlack) * 100.0 : 0;

        // Draw halves
        Imgproc.rectangle(input, new Point(0, 0), new Point(w/2, h), new Scalar(255, 0, 0), 2);
        Imgproc.rectangle(input, new Point(w/2, 0), new Point(w, h), new Scalar(0, 255, 0), 2);

        // Display %
        Imgproc.putText(input, String.format("Left: %.1f%%", leftPercent),
                new Point(30, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);
        Imgproc.putText(input, String.format("Right: %.1f%%", rightPercent),
                new Point(w/2 + 30, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);

        if (!blackContours.isEmpty()) {
            MatOfPoint largestBlack = blackContours.get(0);
            double maxArea = 0;
            for (MatOfPoint contour : blackContours) {
                double area = Imgproc.contourArea(contour);
                if (area > maxArea) {
                    maxArea = area;
                    largestBlack = contour;
                }
                Moments m = Imgproc.moments(largestBlack);
                double cx = m.m10 / m.m00; // x-center of line
                double cy = m.m01 / m.m00;
                Imgproc.circle(input, new Point(cx, cy), 5, new Scalar(255, 255, 0), -1); // cyan dot
                Imgproc.drawContours(input, List.of(largestBlack), -1, new Scalar(0, 255, 0), 2);
            }
        }
        return input;
    }

    public double Left() {
        return leftPercent;
    }

    public double Right() {
        return rightPercent;
    }
}
