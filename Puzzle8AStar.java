import java.util.*;

class Puzzle8Node implements Comparable<Puzzle8Node> {
    int[][] state;
    int gCost; // cost to reach this node
    int hCost; // heuristic cost
    Puzzle8Node parent;

    Puzzle8Node(int[][] state, int gCost, int hCost, Puzzle8Node parent) {
        this.state = state;
        this.gCost = gCost;
        this.hCost = hCost;
        this.parent = parent;
    }

    // Tính cost = gCost + hCost
    int fCost() {
        return gCost + hCost;
    }

    @Override
    public int compareTo(Puzzle8Node other) {
        return Integer.compare(this.fCost(), other.fCost());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Puzzle8Node)) return false;
        Puzzle8Node other = (Puzzle8Node) obj;
        return Arrays.deepEquals(this.state, other.state);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.state);
    }
}

public class Puzzle8AStar {
    // Hướng di chuyển
    static final int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    // Kiểm tra xem trạng thái mới có hợp lệ không
    static boolean isValidMove(int x, int y) {
        return x >= 0 && x < 3 && y >= 0 && y < 3;
    }

    // Đổi chỗ 2 phần tử trong ma trận
    static void swap(int[][] matrix, int x1, int y1, int x2, int y2) {
        int temp = matrix[x1][y1];
        matrix[x1][y1] = matrix[x2][y2];
        matrix[x2][y2] = temp;
    }

    // Tính heuristic (số lượng phần tử không đúng vị trí)
    static int calculateHCost(int[][] state) {
        int hCost = 0;
        int[][] goalState = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] != 0 && state[i][j] != goalState[i][j]) {
                    hCost++;
                }
            }
        }
        return hCost;
    }

    // Tìm các bước di chuyển có thể thực hiện từ một trạng thái
    static List<int[][]> findPossibleMoves(int[][] matrix) {
        List<int[][]> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matrix[i][j] == 0) {
                    for (int[] dir : directions) {
                        int newX = i + dir[0];
                        int newY = j + dir[1];
                        if (isValidMove(newX, newY)) {
                            int[][] newMatrix = new int[3][3];
                            for (int k = 0; k < 3; k++) {
                                newMatrix[k] = matrix[k].clone();
                            }
                            swap(newMatrix, i, j, newX, newY);
                            possibleMoves.add(newMatrix);
                        }
                    }
                    break;
                }
            }
        }
        return possibleMoves;
    }

    // Tìm kiếm A*
    static List<int[][]> aStar(int[][] start) {
        PriorityQueue<Puzzle8Node> openSet = new PriorityQueue<>();
        Set<Puzzle8Node> closedSet = new HashSet<>();
        Map<Puzzle8Node, Puzzle8Node> parentMap = new HashMap<>();

        // khởi tạo trạng thái ban đầu
        Puzzle8Node startNode = new Puzzle8Node(start, 0, calculateHCost(start), null);

        //trạng thái ban đầu
        openSet.add(startNode);
        int step = 1;
        while (!openSet.isEmpty()) {

            Puzzle8Node current = openSet.poll();

            // in ra đỉnh  đang xét
            System.out.println("Bước "+ step++ +" :");
            System.out.println("Đang xét");

            printMatrix(current.state);
            System.out.println();

            // kiểm tra xem trạng thái đang xét có phải là trạng thái đích ko nếu đúng sẽ truy xuất ngược lại các bước thực hiện lưu vết
            if (isGoalState(current.state)) {
                List<int[][]> solution = new ArrayList<>();
                while (current != null) {
                    solution.add(current.state);
                    current = current.parent;
                }
                //hàm đảo ngược các giá trị neeus muoon in ra duong di
                Collections.reverse(solution);
                return solution;
            }
            // thêm trạng thái đã xét vào tập closed
            closedSet.add(current);

            List<int[][]> possibleMoves = findPossibleMoves(current.state);
            for (int[][] move : possibleMoves) {
                Puzzle8Node newNode = new Puzzle8Node(move, current.gCost + 1, calculateHCost(move), current);
                if (!closedSet.contains(newNode)) {
                    openSet.add(newNode);
                    parentMap.put(newNode, current);
                }
            }

            Queue<Puzzle8Node> Opened = new LinkedList<>();

            for (int[][] move : possibleMoves){
                Puzzle8Node newNode = new Puzzle8Node(move, current.gCost + 1, calculateHCost(move), current);
                if (!closedSet.contains(newNode) && newNode != current) {
                    Opened.add(newNode);
                }
            }

            // in ra tập opened
            System.out.println("Opened ");
            for (Puzzle8Node tmp : Opened){
                printMatrix(tmp.state);
                System.out.println();
            }

            Opened.clear();


            // in ra tập closed
            System.out.println("Closed");
            for( Puzzle8Node tmp : closedSet){
                printMatrix(tmp.state);
                System.out.println();
            }
            System.out.println();
        }

        return null;
    }

    // Kiểm tra xem trạng thái hiện tại có phải là trạng thái kết thúc không
    static boolean isGoalState(int[][] matrix) {
        int[][] goalState = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
        return Arrays.deepEquals(matrix, goalState);
    }

    // In ma trận
    static void printMatrix(int[][] matrix) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Chương trình chính
    public static void main(String[] args) {
        int[][] startState = {{2, 8, 3}, {1, 6, 4}, {7, 0, 5}}; // Trạng thái ban đầu

        List<int[][]> solution = aStar(startState);
        int i=1;
        for (int[][] tmp : solution){
            System.out.println("B" + i++);
            printMatrix(tmp);

        }

    }
}
