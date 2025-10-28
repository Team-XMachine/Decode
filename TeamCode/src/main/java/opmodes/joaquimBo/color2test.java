package opmodes.joaquimBo;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Autonomous(name = "Enhanced Color Detection v2.0")
public class color2test extends LinearOpMode {

    // ===== CONSTANTES DE CONFIGURAÇÃO =====
    private static final int CAMERA_WIDTH = 640;
    private static final int CAMERA_HEIGHT = 480;
    private static final double MIN_CONTOUR_AREA = 800;
    private static final double CONFIDENCE_AREA_THRESHOLD = 15000;
    private static final int DETECTION_HISTORY_SIZE = 10;

    // ===== COMPONENTES PRINCIPAIS =====
    private OpenCvWebcam webcam;
    private EnhancedColorPipeline pipeline;
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime buttonCooldown = new ElapsedTime();

    // ===== ESTADOS E CONTROLES =====
    private boolean streamingActive = true;
    private boolean autoModeEnabled = false;

    // ===== ENUMS =====
    public enum DetectionZone {
        LEFT("ESQUERDA", new Scalar(255, 100, 100)),
        CENTER("CENTRO", new Scalar(100, 255, 100)),
        RIGHT("DIREITA", new Scalar(100, 100, 255)),
        NOT_FOUND("NÃO ENCONTRADO", new Scalar(100, 100, 100));

        private final String displayName;
        private final Scalar color;

        DetectionZone(String displayName, Scalar color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public Scalar getColor() { return color; }
    }

    public enum TargetColor {
        BLUE("AZUL", new Scalar(100, 150, 100), new Scalar(130, 255, 255)),
        YELLOW("AMARELO", new Scalar(20, 100, 100), new Scalar(30, 255, 255)),
        RED_LOW("VERMELHO", new Scalar(0, 120, 70), new Scalar(10, 255, 255)),
        RED_HIGH("VERMELHO", new Scalar(170, 120, 70), new Scalar(180, 255, 255)),
        GREEN("VERDE", new Scalar(40, 100, 100), new Scalar(80, 255, 255)),
        ORANGE("LARANJA", new Scalar(10, 100, 100), new Scalar(20, 255, 255));

        private final String name;
        private final Scalar lowerBound;
        private final Scalar upperBound;

        TargetColor(String name, Scalar lowerBound, Scalar upperBound) {
            this.name = name;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public String getName() { return name; }
        public Scalar getLowerBound() { return lowerBound; }
        public Scalar getUpperBound() { return upperBound; }
    }

    @Override
    public void runOpMode() {
        initializeCamera();
        showInitialInstructions();
        waitForStart();

        runtime.reset();

        while (opModeIsActive()) {
            handleGamepadInput();
            updateTelemetry();
            sleep(50); // Otimizado para melhor responsividade
        }

        cleanup();
    }

    // ===== INICIALIZAÇÃO =====
    private void initializeCamera() {
        telemetry.addLine("🔧 Inicializando sistema de visão...");
        telemetry.update();

        try {
            int cameraMonitorViewId = hardwareMap.appContext.getResources()
                    .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

            webcam = OpenCvCameraFactory.getInstance().createWebcam(
                    hardwareMap.get(WebcamName.class, "webcam01"), cameraMonitorViewId);

            pipeline = new EnhancedColorPipeline();
            webcam.setPipeline(pipeline);
            webcam.setMillisecondsPermissionTimeout(8000);

            webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    webcam.startStreaming(CAMERA_WIDTH, CAMERA_HEIGHT, OpenCvCameraRotation.UPRIGHT);
                    streamingActive = true;
                    telemetry.addLine("✅ Sistema de visão ativo!");
                    telemetry.update();
                }

                @Override
                public void onError(int errorCode) {
                    telemetry.addData("❌ Erro na câmera:", getErrorMessage(errorCode));
                    telemetry.update();
                }
            });

        } catch (Exception e) {
            telemetry.addData("❌ Exceção crítica:", e.getMessage());
            telemetry.update();
        }
    }

    // ===== CONTROLES =====
    private void handleGamepadInput() {
        // Cooldown para evitar múltiplos acionamentos
        if (buttonCooldown.milliseconds() < 300) return;

        if (gamepad1.a && streamingActive) {
            webcam.stopStreaming();
            streamingActive = false;
            buttonCooldown.reset();
            telemetry.addLine("⏹️ Streaming pausado");
        }

        if (gamepad1.x && !streamingActive) {
            webcam.startStreaming(CAMERA_WIDTH, CAMERA_HEIGHT, OpenCvCameraRotation.UPRIGHT);
            streamingActive = true;
            buttonCooldown.reset();
            telemetry.addLine("▶️ Streaming retomado");
        }

        if (gamepad1.b) {
            pipeline.cycleTargetColor();
            buttonCooldown.reset();
        }

        if (gamepad1.y) {
            pipeline.resetDetection();
            buttonCooldown.reset();
        }

        if (gamepad1.dpad_up) {
            pipeline.increaseSensitivity();
            buttonCooldown.reset();
        }

        if (gamepad1.dpad_down) {
            pipeline.decreaseSensitivity();
            buttonCooldown.reset();
        }

        if (gamepad1.right_bumper) {
            autoModeEnabled = !autoModeEnabled;
            pipeline.setAutoMode(autoModeEnabled);
            buttonCooldown.reset();
        }
    }

    // ===== TELEMETRIA AVANÇADA =====
    private void updateTelemetry() {
        // Seção de Performance
        telemetry.addData("⚡ PERFORMANCE", "");
        telemetry.addData("├─ FPS", "%.1f", webcam.getFps());
        telemetry.addData("├─ Tempo pipeline", "%.1f ms", webcam.getPipelineTimeMs());
        telemetry.addData("├─ Quadros processados", webcam.getFrameCount());
        telemetry.addData("└─ Runtime", "%.1f s", runtime.seconds());

        // Seção de Detecção
        telemetry.addData("🎯 DETECÇÃO", "");
        telemetry.addData("├─ Cor alvo", pipeline.getCurrentColorName());
        telemetry.addData("├─ Sensibilidade", "%.1f", pipeline.getSensitivity());
        telemetry.addData("├─ Zona", pipeline.getDetectionZone().getDisplayName());
        telemetry.addData("├─ Confiança", "%.1f%%", pipeline.getConfidence());
        telemetry.addData("├─ Área detectada", "%.0f px", pipeline.getDetectedArea());
        telemetry.addData("└─ Objetos encontrados", pipeline.getObjectCount());

        // Posição se detectado
        if (pipeline.isObjectDetected()) {
            Point center = pipeline.getObjectCenter();
            telemetry.addData("📍 POSIÇÃO", "");
            telemetry.addData("├─ Centro X", "%.0f", center.x);
            telemetry.addData("├─ Centro Y", "%.0f", center.y);
            telemetry.addData("└─ Estabilidade", "%.1f%%", pipeline.getStability());
        }

        // Controles
        telemetry.addData("🎮 CONTROLES", "");
        telemetry.addData("├─ A/X", "Parar/Iniciar stream");
        telemetry.addData("├─ B", "Trocar cor (" + pipeline.getCurrentColorName() + ")");
        telemetry.addData("├─ Y", "Reset detecção");
        telemetry.addData("├─ ↑/↓", "Ajustar sensibilidade");
        telemetry.addData("└─ RB", "Modo auto: " + (autoModeEnabled ? "ON" : "OFF"));

        telemetry.update();
    }

    // ===== UTILITÁRIOS =====
    private void showInitialInstructions() {
        telemetry.addLine("🤖 Sistema de Detecção de Cores v2.0");
        telemetry.addLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        telemetry.addLine("✨ Funcionalidades:");
        telemetry.addLine("• Detecção multi-core otimizada");
        telemetry.addLine("• Histórico de detecções");
        telemetry.addLine("• Ajuste de sensibilidade dinâmico");
        telemetry.addLine("• Modo automático inteligente");
        telemetry.addLine("• Interface visual aprimorada");
        telemetry.addLine("");
        telemetry.addLine("⏳ Aguardando start...");
        telemetry.update();
    }

    private String getErrorMessage(int errorCode) {
        // Mensagem genérica mas informativa para todos os erros
        return String.format("Erro de câmera (código: %d). Verifique: conexão USB, permissões, se outra app não está usando a câmera", errorCode);
    }

    private void cleanup() {
        if (webcam != null) {
            webcam.stopStreaming();
            webcam.closeCameraDevice();
        }
    }

    // ===== PIPELINE MELHORADO =====
    class EnhancedColorPipeline extends OpenCvPipeline {

        // Matrizes reutilizáveis para performance
        private final Mat hsvMat = new Mat();
        private final Mat mask = new Mat();
        private final Mat mask2 = new Mat();
        private final Mat combinedMask = new Mat();
        private final Mat hierarchy = new Mat();
        private final Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));

        // Estado da detecção
        private TargetColor[] availableColors = {TargetColor.BLUE, TargetColor.YELLOW, TargetColor.GREEN, TargetColor.ORANGE};
        private int currentColorIndex = 0;
        private double sensitivity = 1.0;
        private boolean autoMode = false;

        // Dados de detecção
        private boolean objectDetected = false;
        private Point objectCenter = new Point(0, 0);
        private double detectedArea = 0;
        private DetectionZone currentZone = DetectionZone.NOT_FOUND;
        private double confidence = 0;
        private int objectCount = 0;
        private double stability = 0;

        // Histórico para estabilização
        private List<Point> centerHistory = new ArrayList<>();
        private List<Double> areaHistory = new ArrayList<>();

        // Performance tracking
        private long frameCount = 0;
        private ElapsedTime fpsTimer = new ElapsedTime();

        @Override
        public Mat processFrame(Mat input) {
            frameCount++;

            try {
                // Conversão para HSV com otimização
                Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);

                // Processamento da máscara
                createOptimizedMask();

                // Encontrar e processar contornos
                List<MatOfPoint> contours = findContours();
                processContours(contours, input);

                // Desenhar interface
                drawEnhancedInterface(input);

                // Atualizar histórico
                updateDetectionHistory();

            } catch (Exception e) {
                drawErrorMessage(input, e);
            }

            return input;
        }

        private void createOptimizedMask() {
            TargetColor currentColor = availableColors[currentColorIndex];

            // Aplicar range de cor com sensibilidade
            Scalar adjustedLower = adjustBounds(currentColor.getLowerBound(), -sensitivity * 10);
            Scalar adjustedUpper = adjustBounds(currentColor.getUpperBound(), sensitivity * 10);

            Core.inRange(hsvMat, adjustedLower, adjustedUpper, mask);

            // Tratar vermelho (que atravessa 0° no HSV)
            if (currentColor == TargetColor.RED_LOW) {
                Core.inRange(hsvMat, TargetColor.RED_HIGH.getLowerBound(), TargetColor.RED_HIGH.getUpperBound(), mask2);
                Core.addWeighted(mask, 1.0, mask2, 1.0, 0.0, combinedMask);
                combinedMask.copyTo(mask);
            }

            // Operações morfológicas para limpeza
            Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
            Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);
        }

        private List<MatOfPoint> findContours() {
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // Filtrar contornos por área mínima
            contours.removeIf(contour -> Imgproc.contourArea(contour) < MIN_CONTOUR_AREA * sensitivity);

            // Ordenar por área (maior primeiro)
            contours.sort((a, b) -> Double.compare(Imgproc.contourArea(b), Imgproc.contourArea(a)));

            return contours;
        }

        private void processContours(List<MatOfPoint> contours, Mat input) {
            objectDetected = false;
            objectCount = contours.size();

            if (!contours.isEmpty()) {
                MatOfPoint largestContour = contours.get(0);
                detectedArea = Imgproc.contourArea(largestContour);

                if (detectedArea > MIN_CONTOUR_AREA) {
                    objectDetected = true;

                    // Calcular centro usando momentos
                    Moments moments = Imgproc.moments(largestContour);
                    if (moments.m00 != 0) {
                        objectCenter.x = moments.m10 / moments.m00;
                        objectCenter.y = moments.m01 / moments.m00;

                        // Determinar zona
                        determineZone(input.cols());

                        // Calcular confiança avançada
                        calculateAdvancedConfidence(largestContour, input);

                        // Desenhar informações
                        drawDetectionInfo(input, contours);
                    }
                }
            }

            if (!objectDetected) {
                currentZone = DetectionZone.NOT_FOUND;
                confidence = 0;
            }
        }

        private void determineZone(int imageWidth) {
            double centerX = objectCenter.x;
            double leftBound = imageWidth * 0.33;
            double rightBound = imageWidth * 0.67;

            if (centerX < leftBound) {
                currentZone = DetectionZone.LEFT;
            } else if (centerX > rightBound) {
                currentZone = DetectionZone.RIGHT;
            } else {
                currentZone = DetectionZone.CENTER;
            }
        }

        private void calculateAdvancedConfidence(MatOfPoint contour, Mat input) {
            // Confiança baseada em múltiplos fatores
            double areaConfidence = Math.min(100, (detectedArea / CONFIDENCE_AREA_THRESHOLD) * 100);
            double shapeConfidence = calculateShapeConfidence(contour);
            double stabilityConfidence = calculateStabilityConfidence();

            confidence = (areaConfidence * 0.4 + shapeConfidence * 0.3 + stabilityConfidence * 0.3);
            confidence = Math.max(0, Math.min(100, confidence));
        }

        private double calculateShapeConfidence(MatOfPoint contour) {
            // Analisar forma do contorno
            double perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
            double circularity = 4 * Math.PI * detectedArea / (perimeter * perimeter);
            return Math.min(100, circularity * 100);
        }

        private double calculateStabilityConfidence() {
            if (centerHistory.size() < 3) return 50;

            double variance = 0;
            Point avgCenter = new Point(0, 0);

            // Calcular centro médio
            for (Point p : centerHistory) {
                avgCenter.x += p.x;
                avgCenter.y += p.y;
            }
            avgCenter.x /= centerHistory.size();
            avgCenter.y /= centerHistory.size();

            // Calcular variância
            for (Point p : centerHistory) {
                double dx = p.x - avgCenter.x;
                double dy = p.y - avgCenter.y;
                variance += Math.sqrt(dx*dx + dy*dy);
            }
            variance /= centerHistory.size();

            stability = Math.max(0, 100 - variance);
            return stability;
        }

        private void updateDetectionHistory() {
            if (objectDetected) {
                centerHistory.add(new Point(objectCenter.x, objectCenter.y));
                areaHistory.add(detectedArea);

                if (centerHistory.size() > DETECTION_HISTORY_SIZE) {
                    centerHistory.remove(0);
                    areaHistory.remove(0);
                }
            }
        }

        private void drawDetectionInfo(Mat input, List<MatOfPoint> contours) {
            // Desenhar todos os contornos detectados
            for (int i = 0; i < Math.min(contours.size(), 5); i++) {
                Scalar color = i == 0 ? new Scalar(0, 255, 0) : new Scalar(100, 100, 255);
                Imgproc.drawContours(input, contours, i, color, 2);
            }

            // Destacar o objeto principal
            if (!contours.isEmpty()) {
                MatOfPoint mainContour = contours.get(0);

                // Centro
                Imgproc.circle(input, objectCenter, 8, new Scalar(255, 0, 0), -1);
                Imgproc.circle(input, objectCenter, 12, new Scalar(255, 255, 255), 2);

                // Bounding box melhorado
                Rect boundingRect = Imgproc.boundingRect(mainContour);
                Imgproc.rectangle(input, boundingRect.tl(), boundingRect.br(), currentZone.getColor(), 3);

                // Informações detalhadas
                String mainInfo = String.format("%s | %.1f%%", currentZone.getDisplayName(), confidence);
                String areaInfo = String.format("Área: %.0f | Objs: %d", detectedArea, objectCount);

                Imgproc.putText(input, mainInfo, new Point(boundingRect.x, boundingRect.y - 25),
                        Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(255, 255, 255), 2);
                Imgproc.putText(input, areaInfo, new Point(boundingRect.x, boundingRect.y - 5),
                        Imgproc.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(200, 200, 200), 1);

                // Trajetória histórica
                drawTrajectoryHistory(input);
            }
        }

        private void drawTrajectoryHistory(Mat input) {
            if (centerHistory.size() > 1) {
                for (int i = 1; i < centerHistory.size(); i++) {
                    Point p1 = centerHistory.get(i-1);
                    Point p2 = centerHistory.get(i);
                    double alpha = (double)i / centerHistory.size();
                    Imgproc.line(input, p1, p2, new Scalar(255 * alpha, 100, 255 * (1-alpha)), 2);
                }
            }
        }

        private void drawEnhancedInterface(Mat input) {
            int width = input.cols();
            int height = input.rows();

            // Linhas de zona mais visíveis
            Imgproc.line(input, new Point(width/3, 0), new Point(width/3, height),
                    new Scalar(150, 150, 150), 2);
            Imgproc.line(input, new Point(2*width/3, 0), new Point(2*width/3, height),
                    new Scalar(150, 150, 150), 2);

            // Labels de zona com fundo
            drawLabelWithBackground(input, "ESQ", new Point(10, 25), DetectionZone.LEFT.getColor());
            drawLabelWithBackground(input, "CENTRO", new Point(width/2 - 30, 25), DetectionZone.CENTER.getColor());
            drawLabelWithBackground(input, "DIR", new Point(2*width/3 + 10, 25), DetectionZone.RIGHT.getColor());

            // Status bar no topo
            String statusText = String.format("Cor: %s | Sens: %.1f | %s | FPS: %.1f",
                    getCurrentColorName(), sensitivity,
                    objectDetected ? "DETECTADO" : "BUSCANDO",
                    webcam != null ? webcam.getFps() : 0);

            drawStatusBar(input, statusText, width, objectDetected);

            // Modo automático indicator
            if (autoMode) {
                drawLabelWithBackground(input, "AUTO", new Point(width - 60, height - 20), new Scalar(0, 255, 255));
            }
        }

        private void drawLabelWithBackground(Mat input, String text, Point position, Scalar color) {
            Size textSize = Imgproc.getTextSize(text, Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, 2, null);
            Imgproc.rectangle(input,
                    new Point(position.x - 2, position.y - textSize.height - 2),
                    new Point(position.x + textSize.width + 2, position.y + 2),
                    new Scalar(0, 0, 0), -1);
            Imgproc.putText(input, text, position, Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, color, 2);
        }

        private void drawStatusBar(Mat input, String text, int width, boolean detected) {
            Scalar bgColor = detected ? new Scalar(0, 100, 0) : new Scalar(100, 0, 0);
            Imgproc.rectangle(input, new Point(0, 0), new Point(width, 30), bgColor, -1);
            Imgproc.putText(input, text, new Point(10, 20),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 1);
        }

        private void drawErrorMessage(Mat input, Exception e) {
            String errorMsg = "ERRO: " + e.getClass().getSimpleName();
            Imgproc.putText(input, errorMsg, new Point(10, 50),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(255, 0, 0), 2);
        }

        private Scalar adjustBounds(Scalar original, double adjustment) {
            return new Scalar(
                    Math.max(0, Math.min(255, original.val[0] + adjustment)),
                    Math.max(0, Math.min(255, original.val[1] + adjustment)),
                    Math.max(0, Math.min(255, original.val[2] + adjustment))
            );
        }

        @Override
        public void onViewportTapped() {
            // Implementar funcionalidade de toque se necessário
            cycleTargetColor();
        }

        // ===== MÉTODOS PÚBLICOS =====
        public void cycleTargetColor() {
            currentColorIndex = (currentColorIndex + 1) % availableColors.length;
            resetDetectionHistory();
        }

        public void increaseSensitivity() {
            sensitivity = Math.min(2.0, sensitivity + 0.1);
        }

        public void decreaseSensitivity() {
            sensitivity = Math.max(0.5, sensitivity - 0.1);
        }

        public void setAutoMode(boolean enabled) {
            autoMode = enabled;
        }

        public void resetDetection() {
            objectDetected = false;
            currentZone = DetectionZone.NOT_FOUND;
            confidence = 0;
            resetDetectionHistory();
        }

        private void resetDetectionHistory() {
            centerHistory.clear();
            areaHistory.clear();
        }

        // ===== GETTERS =====
        public String getCurrentColorName() {
            return availableColors[currentColorIndex].getName();
        }

        public DetectionZone getDetectionZone() { return currentZone; }
        public boolean isObjectDetected() { return objectDetected; }
        public Point getObjectCenter() { return new Point(objectCenter.x, objectCenter.y); }
        public double getDetectedArea() { return detectedArea; }
        public double getConfidence() { return confidence; }
        public double getSensitivity() { return sensitivity; }
        public int getObjectCount() { return objectCount; }
        public double getStability() { return stability; }
    }
}