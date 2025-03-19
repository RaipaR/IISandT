import java.util.Random;

public class SimulatedAnnealingOptimization {
    private static final double INITIAL_TEMPERATURE = 1000;
    private static final double COOLING_RATE = 0.99;
    private static final int ITERATIONS = 1000;
    private static final double MIN_TEMPERATURE = 1e-3;
    
    private Random random = new Random();

    // Функция, которую будем минимизировать: f(x, y) = 1 / (1 + x^2 + y^2)
    private double objectiveFunction(double x, double y) {
        return 1.0 / (1.0 + x * x + y * y);
    }

    public double[] optimize() {
        double x = random.nextDouble() * 10 - 5; // Начальная точка в диапазоне [-5,5]
        double y = random.nextDouble() * 10 - 5;
        double bestX = x, bestY = y;
        double bestValue = objectiveFunction(x, y);
        double temperature = INITIAL_TEMPERATURE;

        while (temperature > MIN_TEMPERATURE) {
            for (int i = 0; i < ITERATIONS; i++) {
                double newX = x + (random.nextDouble() * 2 - 1) * temperature;
                double newY = y + (random.nextDouble() * 2 - 1) * temperature;
                double newValue = objectiveFunction(newX, newY);

                if (acceptanceProbability(bestValue, newValue, temperature) > random.nextDouble()) {
                    x = newX;
                    y = newY;
                    bestValue = newValue;
                    bestX = x;
                    bestY = y;
                }
            }
            temperature *= COOLING_RATE;
        }
        return new double[]{bestX, bestY, bestValue};
    }

    private double acceptanceProbability(double currentEnergy, double newEnergy, double temperature) {
        if (newEnergy < currentEnergy) {
            return 1.0;
        }
        return Math.exp((currentEnergy - newEnergy) / temperature);
    }

    public static void main(String[] args) {
        SimulatedAnnealingOptimization sao = new SimulatedAnnealingOptimization();
        double[] result = sao.optimize();
        System.out.println("Оптимальная точка: x = " + result[0] + ", y = " + result[1]);
        System.out.println("Значение функции: " + result[2]);
    }
}
