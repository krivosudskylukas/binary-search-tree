import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("dictionary.txt"));
        String line;

        // Initializing variables for frequency and array lengths.
        int frequency = 0;
        int arrayLength = 0;
        int fullArrayLength = 0;

        // Reading file and counting frequency and array lengths.
        while((line = br.readLine()) != null) {
            int freq = Integer.parseInt(line.split(" ")[0]);
            fullArrayLength++;
            frequency += freq;
            if(freq > 50000)
                arrayLength++;
        }

        br = new BufferedReader(new FileReader("dictionary.txt"));
        String[][] unsortedWords = new String[fullArrayLength][2];
        int l = 0;

        // Reading file again and storing in 2D String array.
        while((line = br.readLine()) != null) {
            String[] words = line.split(" ");
            unsortedWords[l][0] = words[0];
            unsortedWords[l][1] = words[1];
            l++;
        }

        // Sort words.
        Arrays.sort(unsortedWords, (o1, o2) -> o1[1].compareTo(o2[1]));

        // Initializing k,p,q variables. Sub-frequency is used to calculate dummy key probability.
        String[] k = new String[arrayLength+1];
        double[] p = new double[arrayLength+1];
        double[] q = new double[arrayLength+1];
        int i = 0;
        int subFrequency = 0;

        // Looping through sorted words and calculating probabilities.
        for (String[] arrayElement: unsortedWords) {
            int freq = Integer.parseInt(arrayElement[0]);
            if(freq > 50000){
                k[i+1] = arrayElement[1];
                p[i+1] = (double) freq / (double) frequency;
                q[i] = (double) subFrequency / (double) frequency;
                subFrequency = 0;
                i++;
            }
            else{
                subFrequency += freq;
            }
        }
        //Adding last one, Qn
        q[i] = (double) subFrequency / (double) frequency;

        double[][] e = new double[arrayLength+2][arrayLength+1];
        double[][] w = new double[arrayLength+2][arrayLength+1];
        int[][] root = new int[arrayLength+1][arrayLength+1];

        // Initializing values into matrix diagonal.
        for (l = 1; l < arrayLength+1; l++) {
            e[l][l-1] = q[l-1];
            w[l][l-1] = q[l-1];
        }

        // Printing all words with probabilities
        /*for (l = 0; l < arrayLength+1; l++) {
            System.out.println("Kluc "+k[l]+" pravdepodobnost: "+ p[i] + "dummy: " +q[i]);
        }*/

        int j;
        l = 0;
        i = 0;
        int n = k.length-1;

        for (l = 1; l <= n; l++) {
            for (i = 1; i <= n-l+1; i++) {
                j = i+l-1;
                e[i][j] = Float.MAX_VALUE;
                w[i][j] = w[i][j-1] + p[j] + q[j];
                for (int r = i; r <= j; r++) {
                    double t = e[i][r-1] + e[r+1][j] + w[i][j];
                    if(t < e[i][j]){
                        e[i][j] = t;
                        root[i][j] = r;
                    }
                }
            }
        }

        Stack<NodeClass> Node = new Stack<NodeClass>();

        NodeClass rootNode = new NodeClass(k[root[1][arrayLength-1]],1,arrayLength-1);
        Node.push(rootNode);

        while (!Node.isEmpty()){
            NodeClass newNode = Node.pop();
            l = root[newNode.getLeftIndex()][newNode.getRightIndex()];
            if(l < newNode.getRightIndex()){
                NodeClass nNode = new NodeClass(k[root[l+1][newNode.getRightIndex()]],l+1,newNode.getRightIndex());
                newNode.setRightChild(nNode);
                Node.push(nNode);
            }
            if(newNode.getLeftIndex() < l){
                NodeClass nNode = new NodeClass(k[root[newNode.getLeftIndex()][l-1]], newNode.getLeftIndex(), l-1);
                newNode.setLeftChild(nNode);
                Node.push(nNode);
            }
        }

        rootNode.prnt(rootNode);

        while(true){
            Scanner myObj = new Scanner(System.in);  // Create a Scanner object
            System.out.println("\nIf you wish to continue enter desired word, else type break. ");
            String word = myObj.nextLine();
            int comparisons = pocet_porovnani(rootNode, word,0);
            if(comparisons > 0)
                System.out.println("Word "+word+" was found in "+comparisons+" comparisons.");
            else
                System.out.println("Word "+word+" was not found. It took "+(-1*comparisons)+" comparisons.");

            if(word.equals("break"))
                break;
        }

    }




    private static int pocet_porovnani(NodeClass rootNode, String word, int i){
        if(rootNode.getNode().equals(word)){
            return i+1;
        }
        else if (rootNode.getNode().compareTo(word) < 0) {
            if(rootNode.getRightChild() != null)
                return pocet_porovnani(rootNode.getRightChild(), word, i + 1);
            else
                return -1*(i+1);
        }
        else if(rootNode.getNode().compareTo(word) > 0){
            if(rootNode.getLeftChild() != null)
                return pocet_porovnani(rootNode.getLeftChild(),word,i+1);
            else
                return -1*(i+1);
        }
        return 0;
    }



}

class NodeClass {
    private String node;
    private int leftIndex;
    private int rightIndex;
    private NodeClass rightChild;
    private NodeClass leftChild;

    public NodeClass getRightChild() {
        return rightChild;
    }

    public void setRightChild(NodeClass rightChild) {
        this.rightChild = rightChild;
    }

    public NodeClass getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(NodeClass leftChild) {
        this.leftChild = leftChild;
    }

    public NodeClass(String node, int leftIndex, int rightIndex) {
        this.node = node;
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
    }

    public String getNode() {
        return node;
    }

    public int getLeftIndex() {
        return leftIndex;
    }

    public int getRightIndex() {
        return rightIndex;
    }

    public void prnt(NodeClass root){
        Queue<NodeClass> queue = new LinkedList<NodeClass>();
        queue.add(root);
        queue.add(null);
        System.out.println("\n-------------------------------------------------------------------------------------------------");
        while (!queue.isEmpty()) {
            NodeClass temp = queue.poll();
            if(temp!=null)
                System.out.print(temp.getNode() + " ");
            if(temp == null) {
                System.out.println();
                if(queue.isEmpty()) break;
                queue.add(null);
                continue;
            }
            if (temp.getLeftChild() != null) {
                queue.add(temp.getLeftChild());
            }
            if (temp.getRightChild() != null) {
                queue.add(temp.getRightChild());
            }
        }
    }
}


