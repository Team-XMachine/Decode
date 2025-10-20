package opmodes.Jair;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Core;

import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@TeleOp(name = "OpenCV Detecção de Cores")
public class Color extends LinearOpMode {

    OpenCvWebcam webcam;
    ColorDetectionPipeline pipeline;

    // Enum para direções \\
    public enum Direction {
        LEFT("ESQUERDA"),
        CENTER("CENTRO"),
        RIGHT("DIREITA"),
        NOT_FOUND("NÃO ENCONTRADO");

        private final String displayName;
        Direction(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    @Override
    public void runOpMode() {
        // DEBUG: Log de inicialização \\
        telemetry.addLine("🔧 MODO DEBUG: Iniciando câmera...");
        telemetry.update();

        try {
            // Inicialização da câmera com tratamento de erro \\
            int cameraMonitorViewId = hardwareMap.appContext.getResources()
                    .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

            webcam = OpenCvCameraFactory.getInstance().createWebcam(
                    hardwareMap.get(WebcamName.class, "webcam01"), cameraMonitorViewId);

            pipeline = new ColorDetectionPipeline();
            webcam.setPipeline(pipeline);
            webcam.setMillisecondsPermissionTimeout(5000);

            webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {

                @Override
                public void onOpened() {
                    webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
                    telemetry.addLine("✅ Câmera inicializada com sucesso!");
                    telemetry.update();
                }

                @Override
                public void onError(int errorCode) {
                    telemetry.addData("❌ ERRO na câmera, código:", errorCode);
                    telemetry.update();
                }
            });

        } catch (Exception e) {
            telemetry.addData("❌ EXCEÇÃO na inicialização:", e.getMessage());
            telemetry.update();
        }

        telemetry.addLine("📋 CONTROLES:");
        telemetry.addLine("A - Parar streaming");
        telemetry.addLine("B - Alternar cor alvo");
        telemetry.addLine("Y - Resetar detecção");
        telemetry.addLine("⏳ Aguardando start...");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Controles do gamepad \\
            if (gamepad1.a) {
                webcam.stopStreaming();
                telemetry.addLine("⏹️ Streaming parado");
            }

            if (gamepad1.b) {
                pipeline.toggleTargetColor();
                telemetry.addLine("🎨 Cor alterada para: " + pipeline.getCurrentColorName());
            }

            if (gamepad1.y) {
                pipeline.resetDetection();
                telemetry.addLine("🔄 Detecção resetada");
            }

            // Telemetria de debug melhorada \\
            displayDebugInfo();

            sleep(100); // Reduzido para mais responsividade \\
        }
    }

    private void displayDebugInfo() {

        telemetry.addData("📊 STATUS GERAL", "");
        telemetry.addData("├─ FPS", String.format("%.1f", webcam.getFps()));
        telemetry.addData("├─ Quadros processados", webcam.getFrameCount());
        telemetry.addData("└─ Pipeline (ms)", webcam.getPipelineTimeMs());

        telemetry.addData("🎯 DETECÇÃO", "");
        telemetry.addData("├─ Cor alvo", pipeline.getCurrentColorName());
        telemetry.addData("├─ Direção", pipeline.getDirection().getDisplayName());
        telemetry.addData("├─ Confiança", String.format("%.1f%%", pipeline.getConfidence()));
        telemetry.addData("└─ Área detectada", pipeline.getDetectedArea());

        if (pipeline.isObjectDetected()) {
            Point center = pipeline.getObjectCenter();
            telemetry.addData("📍 POSIÇÃO", "");
            telemetry.addData("├─ Centro X", (int)center.x);
            telemetry.addData("└─ Centro Y", (int)center.y);
        }

        telemetry.update();
    }

    class ColorDetectionPipeline extends OpenCvPipeline {

        private Mat hsvMat = new Mat();
        private Mat mask = new Mat();
        private Mat hierarchy = new Mat();
        // Cores disponíveis (HSV)

        private final Scalar[] LOWER_BOUNDS = {
                new Scalar(100, 100, 100), // Azul \\
                new Scalar(36, 100, 100),  // Amarelo \\
                new Scalar(0, 100, 100)    // Vermelho \\
        };

        private final Scalar[] UPPER_BOUNDS = {
                new Scalar(130, 255, 255), // Azul \\
                new Scalar(70, 255, 255),  // Amarelo \\
                new Scalar(10, 255, 255)   // Vermelho \\
        };

        private final String[] COLOR_NAMES = {"AZUL", "AMARELO", "VERMELHO"};

        private int currentColorIndex = 0;
        private boolean objectDetected = false;
        private Point objectCenter = new Point(0, 0);
        private double detectedArea = 0;
        private Direction currentDirection = Direction.NOT_FOUND;
        private double confidence = 0;
        private boolean viewportPaused = false;

        @Override
        public Mat processFrame(Mat input) {

            try {
                // Conversão para HSV \\
                Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);

                // Criar máscara para a cor atual \\
                Core.inRange(hsvMat,
                        LOWER_BOUNDS[currentColorIndex],
                        UPPER_BOUNDS[currentColorIndex],
                        mask);

                // Encontrar contornos \\
                List<org.opencv.core.MatOfPoint> contours = new ArrayList<>();
                Imgproc.findContours(mask, contours, hierarchy,
                        Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                // Resetar detecção \\
                objectDetected = false;
                detectedArea = 0;
                confidence = 0;

                // Encontrar maior contorno \\
                if (!contours.isEmpty()) {
                    double maxArea = 0;
                    org.opencv.core.MatOfPoint largestContour = null;

                    for (org.opencv.core.MatOfPoint contour : contours) {
                        double area = Imgproc.contourArea(contour);
                        if (area > maxArea) {
                            maxArea = area;
                            largestContour = contour;
                        }
                    }
                    // Processar se encontrou contorno significativo \\
                    if (largestContour != null && maxArea > 500) { // Threshold mínimo \\
                        objectDetected = true;
                        detectedArea = maxArea;

                        // Calcular centro do objeto \\
                        Moments moments = Imgproc.moments(largestContour);
                        if (moments.m00 != 0) {
                            objectCenter.x = moments.m10 / moments.m00;
                            objectCenter.y = moments.m01 / moments.m00;

                            // Determinar direção \\
                            determineDirection(input.cols());

                            // Calcular confiança baseada na área \\
                            confidence = Math.min(100, (maxArea / 10000) * 100);
                        }

                        // Desenhar contorno e informações \\
                        drawDetectionInfo(input, largestContour);
                    } else {
                        currentDirection = Direction.NOT_FOUND;
                    }
                }

                // Desenhar a interface \\
                drawInterface(input);

            } catch (Exception e) {
                // Em caso de erro, desenhar mensagem \\
                Imgproc.putText(input, "ERRO: " + e.getMessage(),
                        new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5,
                        new Scalar(255, 0, 0), 1);
            }

            return input;
        }

        private void determineDirection(int imageWidth) {
            int centerX = (int) objectCenter.x;
            int leftBoundary = imageWidth / 3;
            int rightBoundary = 2 * imageWidth / 3;

            if (centerX < leftBoundary) {
                currentDirection = Direction.LEFT;
            } else if (centerX > rightBoundary) {
                currentDirection = Direction.RIGHT;
            } else {
                currentDirection = Direction.CENTER;
            }
        }

        private void drawDetectionInfo(Mat input, org.opencv.core.MatOfPoint contour) {
            // Desenhar contorno \\
            List<org.opencv.core.MatOfPoint> contours = new ArrayList<>();
            contours.add(contour);
            Imgproc.drawContours(input, contours, -1, new Scalar(0, 255, 0), 2);

            // Desenhar centro \\
            Imgproc.circle(input, objectCenter, 5, new Scalar(255, 0, 0), -1);

            // Desenhar bounding box \\
            Rect boundingRect = Imgproc.boundingRect(contour);
            Imgproc.rectangle(input, boundingRect.tl(), boundingRect.br(),
                    new Scalar(255, 255, 0), 2);

            // Texto com informações \\
            String info = String.format("%s - %.0f%%", currentDirection.getDisplayName(), confidence);
            Imgproc.putText(input, info,
                    new Point(boundingRect.x, boundingRect.y - 10),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 2);
        }

        private void drawInterface(Mat input) {
            int width = input.cols();
            int height = input.rows();

            // Linhas divisórias para direção \\
            Imgproc.line(input, new Point(width/3, 0), new Point(width/3, height),
                    new Scalar(100, 100, 100), 1);
            Imgproc.line(input, new Point(2*width/3, 0), new Point(2*width/3, height),
                    new Scalar(100, 100, 100), 1);

            // Labels de direção \\
            Imgproc.putText(input, "ESQ", new Point(10, 20),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(255, 255, 255), 1);
            Imgproc.putText(input, "CENTRO", new Point(width/2 - 20, 20),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(255, 255, 255), 1);
            Imgproc.putText(input, "DIR", new Point(2*width/3 + 10, 20),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(255, 255, 255), 1);

            // Status da detecção \\
            String status = String.format("Cor: %s | %s",
                    COLOR_NAMES[currentColorIndex],
                    objectDetected ? "DETECTADO" : "BUSCANDO");
            Imgproc.putText(input, status, new Point(10, height - 10),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.4,
                    objectDetected ? new Scalar(0, 255, 0) : new Scalar(0, 0, 255), 1);
        }

        @Override
        public void onViewportTapped() {
            viewportPaused = !viewportPaused;
            if (viewportPaused) {
                webcam.pauseViewport();
            } else {
                webcam.resumeViewport();
            }
        }


        // Métodos públicos para telemetria \\
        public void toggleTargetColor() {
            currentColorIndex = (currentColorIndex + 1) % COLOR_NAMES.length;
        }
        
        public String getCurrentColorName() {
            return COLOR_NAMES[currentColorIndex];
        }

        public Direction getDirection() {
            return currentDirection;
        }

        public boolean isObjectDetected() {
            return objectDetected;
        }

        public Point getObjectCenter() {
            return objectCenter;
        }

        public double getDetectedArea() {
            return detectedArea;
        }

        public double getConfidence() {
            return confidence;
        }

        public void resetDetection() {
            objectDetected = false;
            currentDirection = Direction.NOT_FOUND;
            confidence = 0;
        }
    }
}