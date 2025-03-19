import java.util.Random;
import java.util.Arrays;

public class AntColonyTSP {
    private static final int NUM_CITIES = 5;
    private static final int NUM_ANTS = 10;
    private static final int MAX_ITERATIONS = 100;
    private static final double ALPHA = 1.0;  // Влияние феромонов
    private static final double BETA = 2.0;   // Влияние эвристики (1/расстояние)
    private static final double EVAPORATION = 0.5; // Коэффициент испарения феромонов
    private static final double Q = 100; // Количество откладываемого феромона
    private static final double INITIAL_PHEROMONE = 1.0;

    private double[][] distances;
    private double[][] pheromones;
    private Random random = new Random();

    public AntColonyTSP(double[][] distances) {
        this.distances = distances;
        pheromones = new double[NUM_CITIES][NUM_CITIES];
        for (int i = 0; i < NUM_CITIES; i++) {
            Arrays.fill(pheromones[i], INITIAL_PHEROMONE);
        }
    }

    public int[] solve() {
        int[] bestTour = null;
        double bestTourLength = Double.MAX_VALUE;

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            int[][] tours = new int[NUM_ANTS][NUM_CITIES];
            double[] tourLengths = new double[NUM_ANTS];

            for (int ant = 0; ant < NUM_ANTS; ant++) {
                tours[ant] = constructTour();
                tourLengths[ant] = calculateTourLength(tours[ant]);
                if (tourLengths[ant] < bestTourLength) {
                    bestTourLength = tourLengths[ant];
                    bestTour = tours[ant].clone();
                }
            }
            updatePheromones(tours, tourLengths);
        }
        return bestTour;
    }

    private int[] constructTour() {
        int[] tour = new int[NUM_CITIES];
        boolean[] visited = new boolean[NUM_CITIES];
        int currentCity = random.nextInt(NUM_CITIES);
        tour[0] = currentCity;
        visited[currentCity] = true;

        for (int i = 1; i < NUM_CITIES; i++) {
            int nextCity = selectNextCity(currentCity, visited);
            tour[i] = nextCity;
            visited[nextCity] = true;
            currentCity = nextCity;
        }
        return tour;
    }

    private int selectNextCity(int currentCity, boolean[] visited) {
        double[] probabilities = new double[NUM_CITIES];
        double sum = 0.0;
        
        for (int city = 0; city < NUM_CITIES; city++) {
            if (!visited[city]) {
                probabilities[city] = Math.pow(pheromones[currentCity][city], ALPHA) * 
                                     Math.pow(1.0 / distances[currentCity][city], BETA);
                sum += probabilities[city];
            }
        }
        
        double rand = random.nextDouble() * sum;
        double cumulative = 0.0;
        for (int city = 0; city < NUM_CITIES; city++) {
            if (!visited[city]) {
                cumulative += probabilities[city];
                if (cumulative >= rand) {
                    return city;
                }
            }
        }
        return -1; 
    }

    private double calculateTourLength(int[] tour) {
        double length = 0.0;
        for (int i = 0; i < NUM_CITIES - 1; i++) {
            length += distances[tour[i]][tour[i + 1]];
        }
        length += distances[tour[NUM_CITIES - 1]][tour[0]];
        return length;
    }

    private void updatePheromones(int[][] tours, double[] tourLengths) {
        for (int i = 0; i < NUM_CITIES; i++) {
            for (int j = 0; j < NUM_CITIES; j++) {
                pheromones[i][j] *= (1 - EVAPORATION);
            }
        }
        for (int ant = 0; ant < NUM_ANTS; ant++) {
            double pheromoneDeposit = Q / tourLengths[ant];
            for (int i = 0; i < NUM_CITIES - 1; i++) {
                pheromones[tours[ant][i]][tours[ant][i + 1]] += pheromoneDeposit;
                pheromones[tours[ant][i + 1]][tours[ant][i]] += pheromoneDeposit;
            }
            pheromones[tours[ant][NUM_CITIES - 1]][tours[ant][0]] += pheromoneDeposit;
            pheromones[tours[ant][0]][tours[ant][NUM_CITIES - 1]] += pheromoneDeposit;
        }
    }

    public static void main(String[] args) {
        double[][] distances = {
            {0, 10, 15, 20, 25},
            {10, 0, 35, 25, 30},
            {15, 35, 0, 30, 40},
            {20, 25, 30, 0, 20},
            {25, 30, 40, 20, 0}
        };

        AntColonyTSP aco = new AntColonyTSP(distances);
        int[] bestTour = aco.solve();

        System.out.println("Лучший найденный маршрут: " + Arrays.toString(bestTour));
    }
}
