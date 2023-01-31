import java.io.*;

import java.util.ArrayList;
import java.util.List;


public class bplustree {

    public static int D; //degree
    public static String leave = "LeafNode: ";

    public static int index_num = 0; //인덱스 넘버 매길 ㄸ ㅐ사용

    public static int find_index() {
        if ((D % 2) == 0) {
            return D / 2;
        } else {
            int i = (int) (Math.ceil((double) D / (double) 2) - 1);
            return i;
        }
    }

    public static class Node { //index node //여기에 index node tree 저장?
        int m = 0;
        nLNode parent;


        public Node() {
            this.m = 0;
            this.parent = null;
        }

        public boolean notLeaf(Node node) {
            if (node instanceof nLNode) {
                return true; //nonleaf
            }
            return false; //leaf
        }

        public void insert_key(int key, int value) throws CloneNotSupportedException {}

    }


    public static class nLNode extends Node { //nonleaf node
        List<pair> p;


        Node rm_childNode;

        public nLNode(int key, int value) {
            m++;
            p = new ArrayList<>();
            p.add(new pair(key, value));

        }

        public nLNode() {
            p = new ArrayList<>();
        }

        public void insert_key(int key, int value) throws CloneNotSupportedException {

            Node node = Tree.root;
            while (node.notLeaf(node)) { //node 가 leaf 면 나가짐!!
                //nLNode nLNode = (nLNode)node;
                if (key > ((nLNode) node).p.get(((nLNode) node).p.size() - 1).key) { //key가 맨마지막 제일 큰 원소보다 크면 그냥 insert
                    node = ((nLNode) node).rm_childNode;

                } else {
                    for (int i = 0; i < ((nLNode) node).p.size(); i++) {
                        if (key < ((nLNode) node).p.get(i).key) { //i번째 key가 들어온 key 값보다 클때
                            node = ((nLNode) node).p.get(i).l_child;
                            break;
                        }
                        else if(key == ((nLNode) node).p.get(i).key){
                            node = ((nLNode)node).rm_childNode;
                            break;
                        }
                    }
                    continue;
                }
            }
            LNode leafNode = (LNode) node; //leaf 가 됐을 경우
            leafNode.insert_key(key, value);

        }

        public void insert_(nLNode node) throws CloneNotSupportedException { //내가 nonleaf 에서 overflow 걸린 상태


            while (node.p.size() > D - 1) { //사이즈가 클 동안...

                nLNode left = node;
                nLNode right = new nLNode();
                int index = find_index();
                nLNode parentNode = new nLNode();
                parentNode.p.add(node.p.get(index).clone());
                parentNode.m++;

                for (int i = index + 1; i < D; i++) {
                    right.p.add(left.p.get(i).clone());
                    right.m++;
                }

                //right의 rightmost node는 기존 node의 rightmost node 이다
                //left의 rightmost node는 기존 node에서 index에 있는 left child 이다.
                int index2 = index;
                for (int i = 0; i < right.p.size(); i++) {
                    right.p.get(i).l_child = node.p.get(++index2).l_child;
                }


                for (int i = D - 1; i >= index; i--) {
                    left.p.remove(i);
                    left.m--;
                }

                right.rm_childNode = node.rm_childNode;
                left.rm_childNode = parentNode.p.get(0).l_child;
                parentNode.p.get(0).l_child = left;

                right.rm_childNode.parent = right;

                for (int i = 0; i < right.p.size(); i++) {
                    (right.p.get(i).l_child).parent = right;
                }

                if (node == Tree.root) {


                    left.parent = parentNode;
                    right.parent = parentNode;
                    parentNode.rm_childNode = right;

                    Tree.root = parentNode;
                    node = parentNode;
                } else { //이 인덱스노드도 부모가 있는 상황
                    nLNode parent = node.parent;
                    for (int i = 0; i < parent.p.size(); i++) {

                        if (parentNode.p.get(0).key < parent.p.get(i).key) { //i번째 key가 들어온 key 값보다 클때
                            pair pair = parentNode.p.get(0).clone();
                            parent.p.add(i, pair);
                            parent.m++;
                            parent.p.get(i + 1).l_child = right;
                            break;
                        }

                        if (i == parent.p.size() - 1) { //i번째 key가 들어온 key 값보다 클때
                            pair pair = parentNode.p.get(0).clone();
                            parent.p.add(pair);
                            parent.m++;
                            parent.rm_childNode = right;
                            break;
                        }

                    }

                    left.parent = parent;
                    right.parent = parent;
                    node = parent;
                }

            }
        }
    }

    public static class LNode extends Node { //leaf node
        List<pair> p_;
        LNode r_siblingNode;


        public LNode(int key, int value) {

            p_ = new ArrayList<>();
            p_.add(new pair(key, value));
            this.m++;
            r_siblingNode = null;
        }

        public LNode() {
            p_ = new ArrayList<>();
            r_siblingNode = null;
        }


        public void insert_key(int key, int value) throws CloneNotSupportedException {

            for (int i = 0; i < this.p_.size(); i++) {

                if (p_.get(i).key == key) {
                    return;
                }

                if (key > p_.get(p_.size() - 1).key) { //key가 맨마지막 제일 큰 원소보다 크면 그냥 insert
                    this.p_.add(new pair(key, value));
                    this.m++;
                    break;
                }

                if (key < p_.get(i).key) { //i번째 key가 들어온 key 값보다 클때
                    this.p_.add(i, new pair(key, value));
                    this.m++;
                    break;
                }
                if (p_.get(i).key == key) {
                    return;
                }
            }

            LNode left = this;
            LNode right = new LNode();

            if (m > D - 1) { //overflow일 경우 node split
                int index = find_index();//split 하고 올라갈 친구

                for (int i = index; i < this.p_.size(); i++) {
                    try {
                        right.p_.add(p_.get(i).clone());
                    } catch (CloneNotSupportedException e) {
                        throw new RuntimeException(e);
                    }
                    right.m++;
                }
                for (int i = this.p_.size() - 1; i >= index; i--) {
                    left.p_.remove(i);
                    left.m--;
                }
                if (Tree.root == this) {

                    nLNode parent = new nLNode();

                    parent.p.add(right.p_.get(0).clone());
                    parent.p.get(0).l_child = left;
                    parent.m++;
                    parent.rm_childNode = right;
                    right.r_siblingNode = this.r_siblingNode;
                    left.r_siblingNode = right;
                    right.parent = parent;
                    left.parent = parent;
                    Tree.root = parent;


                } else {
                    //overflow된 이 leaf노드가 root 가 아니라면
                    //parent 노드를 선언하고 parent의 size만큼 for문을 돌면서
                    nLNode parent = (nLNode) this.parent;
                    pair pair = right.p_.get(0).clone();
                    for (int i = 0; i < parent.p.size(); i++) {
                        if (right.p_.get(0).key < parent.p.get(i).key) { //i번째 key가 들어온 key 값보다 클때
                            parent.p.add(i, pair);
                            parent.m++;
                            parent.p.get(i).l_child = left;
                            parent.p.get(i + 1).l_child = right;

                            break;
                        }


                        if (i == (parent.p.size() - 1)) {
                            parent.p.add(pair);
                            parent.m++;
                            parent.p.get(parent.p.size() - 1).l_child = left;
                            parent.rm_childNode = right;
                            break;

                        }


                    }
                    right.r_siblingNode = this.r_siblingNode;
                    left.r_siblingNode = right;

                    left.parent = parent;
                    right.parent = parent;

                }
                nLNode temp = left.parent;
                if (temp.p.size() > D - 1) {
                    try {
                        temp.insert_(temp);
                    } catch (CloneNotSupportedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static void singleKey_Search(String indexFilen, int search_key) {
        try {
            readFile(indexFilen);

            Node start = Tree.root;

            if (start == null) {
                System.out.println("There is no tree.");
                return;
            }

            while (start.notLeaf(start)) { //node 가 leaf 면 나가짐!!

                for (pair p : ((nLNode) start).p) {
                    System.out.print(p.key + ",");
                }
                System.out.println("");
                if (search_key > ((nLNode) start).p.get(((nLNode) start).p.size() - 1).key) { //key가 맨마지막 제일 큰 원소보다 크면 그냥 insert
                    start = ((nLNode) start).rm_childNode;
                    continue;
                } else {

                    for (int i = 0; i < ((nLNode) start).p.size(); i++) {
                        if (search_key == ((nLNode) start).p.get(i).key) {
                            if (i + 1 < ((nLNode) start).p.size()) {
                                start = ((nLNode) start).p.get(i + 1).l_child;
                                break;
                            } else {
                                start = ((nLNode) start).rm_childNode;
                                break;
                            }
                        } else if (search_key < ((nLNode) start).p.get(i).key) { //i번째 key가 들어온 key 값보다 클때
                            start = ((nLNode) start).p.get(i).l_child;
                            break;
                        }
                    }
                    continue;
                }
            }

            if (start instanceof LNode) {
                for (pair p : ((LNode) start).p_) {
                    if (search_key == p.key) {
                        System.out.println(p.value);
                    }
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void rangedKey_Search(String indexfilen, int start, int finish) {
        try {
            readFile(indexfilen);

            Node node = Tree.root;

            //100에 가깝고 큰 leaf노드 찾아서 내려가기
            while (node.notLeaf(node)) { //node 가 leaf 면 나가짐!!

                if (start > ((nLNode) node).p.get(((nLNode) node).p.size() - 1).key) { //key가 맨마지막 제일 큰 원소보다 크면 그냥 insert
                    node = ((nLNode) node).rm_childNode;
                    continue;
                }
                else {

                    for (int i = 0; i < ((nLNode) node).p.size(); i++) {
                        if (start == ((nLNode) node).p.get(i).key) {
                            if (i + 1 < ((nLNode) node).p.size()) {
                                node = ((nLNode) node).p.get(i + 1).l_child;
                                break;
                            } else {
                                node = ((nLNode) node).rm_childNode;
                                break;
                            }
                        } else if (start < ((nLNode) node).p.get(i).key) { //i번째 key가 들어온 key 값보다 클때
                            node = ((nLNode) node).p.get(i).l_child;
                            break;
                        }
                    }
                    continue;
                }
            }

            while(true){
                LNode lNode = (LNode) node;
                for (int i = 0; i < lNode.p_.size(); i++){
                    if(lNode.p_.get(i).key < start){
                        continue;
                    }

                    else if(start <= lNode.p_.get(i).key && finish >= lNode.p_.get(i).key){
                        System.out.println(lNode.p_.get(i).key + "," + lNode.p_.get(i).value);
                    }
                    else{
                        return;
                    }
                }

                if(((LNode) node).r_siblingNode != null){
                    node = ((LNode) node).r_siblingNode;
                }
                else{
                    break;
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }


    }

    public static void createFile(String fileName, int degree) {
        try {
            File indexfile = new File("./"+fileName);
            BufferedWriter file = new BufferedWriter(new FileWriter(indexfile, false)); //false는 새로 작성
            D = degree;
            file.write("degree: " + degree);
            file.write("\n");
            file.flush();
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void readFile(String indexfilen) throws IOException, CloneNotSupportedException { //input.csv의 key value 읽기
        List<Integer> leaf_index = new ArrayList<>();
        String leafline = "";
        File indexfile = new File("./"+indexfilen);
        BufferedReader br = new BufferedReader(new FileReader(indexfile));

        /*
        만약 기존의 Tree가 있을 경우 먼저 leaf 노드부터 파악한다
        줄이 LeafNode:로 시작할 경우 ","를 기준으로 나누어서
        leaf_index에 담아준다.
        */
        while ((leafline = br.readLine()) != null) {
            String arr[] = leafline.split(" ");
            if (arr[0].equals("LeafNode:")) {
                String leaf[] = arr[1].split(",");
                for (int i = 0; i < leaf.length; i++) {
                    leaf_index.add(Integer.parseInt(leaf[i]));
                }
                break;
            }
        }

        br.close();

        String line1 = "";

        /* 다시 새롭게 파일을 읽어들인다.*/
        Node node = new Node(); //임시 null 노드

        /*root에 저장하기 전에 기존 트리를 인덱스에 맞게 List에 저장해준다.
          단 인덱스를 1번부터 시작한다고 명시했으므로 0번째 인덱스는 null로 처리한다.
         */
        List<Node> temp_array = new ArrayList<>(); //임의로 인덱스에 맞게 노드를 저장할 리스트
        temp_array.add(null); //0번째 인덱스는 null처리

        BufferedReader br1 = new BufferedReader(new FileReader(indexfile));

        /* line이 끝나기 전까지 읽는다
        1. 만약 degree: 로 시작한다면 degree 를 저장한다.
        2. 만약 #으로 시작한다면 key, value를 담아준다.
           이때 해당 인덱스가 leaf_index에 있다면 LNode 객체에 담아주고
           아니라면 nLNond 객체에 담아준다.
        3. 어떤 클래스의 객체인지 확인 후 leafNode나 nonLeafNode를 temp_array에 담는다.
        4. 만약 현재 인덱스에 해당하는 temp_array의 객체가 LNode의 객체라면 child는 LNode로, parent는 nLNode로 형변환을 해준다.
           child의 key와 parent의 key를 비교하며 부모자식 관계를 정리한다.
        5. 만약 현재 인덱스가 nLNode의 객체라면 parent와 child 둘다 nLNode의 객체로 형변환을 해주고 부모자식 관계를 정리한다.
        6. 만약 LeafNode: 로 시작한다면 leaf Node 끼리 sibling 관계를 정리한다.
        7.
        */
        while ((line1 = br1.readLine()) != null) {

            Node tempNode = new Node();

            String arr1[] = line1.split(" ");

            if (arr1[0].equals("degree:")) {
                D = Integer.parseInt(arr1[1]);
            }

            if (arr1[0].equals("#")) {
                LNode leafNode = new LNode();
                nLNode nLeafNode = new nLNode();

                for (int i = 3; i < arr1.length; i++) {

                    String node_arr[] = arr1[i].split(",");
                    int key = Integer.parseInt(node_arr[0]);
                    int value = Integer.parseInt(node_arr[1]);

                    if (leaf_index.contains(Integer.parseInt(arr1[2]))) {
                        leafNode.p_.add(new pair(key, value));

                    } else {
                        nLeafNode.p.add(new pair(key, value));
                    }
                }
                if (leaf_index.contains(Integer.parseInt(arr1[2]))) {
                    temp_array.add(leafNode);
                } else {
                    temp_array.add(nLeafNode);
                }


                if (temp_array.get(Integer.parseInt(arr1[2])) instanceof LNode) {
                    LNode child = (LNode) temp_array.get(Integer.parseInt((arr1[2])));
                    nLNode parent = (nLNode) temp_array.get(Integer.parseInt(arr1[1]));

                    for (pair pair : parent.p) {
                        if (child.p_.get(0).key < pair.key) {
                            pair.l_child = child;
                            child.parent = parent;
                            break;
                        }
                    }
                    if (child.parent == null) {
                        child.parent = parent;
                        parent.rm_childNode = child;
                    }

                } else {
                    nLNode child = (nLNode) temp_array.get(Integer.parseInt((arr1[2])));
                    nLNode parent = (nLNode) temp_array.get(Integer.parseInt(arr1[1]));

                    if (parent == null) {
                        continue;
                    }

                    for (pair pair : parent.p) {
                        if (child.p.get(0).key < pair.key) {
                            pair.l_child = child;
                            child.parent = parent;

                        }
                    }
                    if (child.parent == null) {
                        child.parent = parent;
                        parent.rm_childNode = child;
                    }


                }

            }


            if (arr1[0].equals("LeafNode:")) {
                for (int i = 0; i < leaf_index.size(); i++) {
                    if(i == leaf_index.size()-1){
                        LNode leaf = (LNode) temp_array.get(leaf_index.get(i));
                        leaf.r_siblingNode = null;
                        break;
                    }

                    LNode leaf = (LNode) temp_array.get(leaf_index.get(i));
                    LNode nextLeaf = (LNode) temp_array.get(leaf_index.get(i + 1));
                    leaf.r_siblingNode = nextLeaf;

                }
            }
        }
        /*만약 temp_array의 size가 2보다 작다면 들어온 요소가 없으므로 root를 null로 설정
        만약 temp_array의 size가 2이고 첫번째 요소가 LNode의 객체라면 LNode 형 root를 만든다.
        그 외에는 nLNode node를 root에 대입한다.
         */

        if (temp_array.size() < 2) {
            Tree.root = null;
        } else if (temp_array.size() == 2 && temp_array.get(1) instanceof LNode) { //null을 포함한 list의 개수가 2개면 노드가 1개뿐이니 root이다
            LNode lNode = (LNode) temp_array.get(1);
            Tree.root = lNode;
        } else if (temp_array.get(1) instanceof nLNode) {
            nLNode nLNode = (nLNode) temp_array.get(1);
            Tree.root = nLNode;
        }

        br.close();

    }


    public static class pair implements Cloneable {
        int key;
        int value;
        Node l_child; // 왼쪽 자식 노드


        public pair(int key, int value) {
            this.key = key;
            this.value = value;
            this.l_child = null;
        }

        public pair clone() throws CloneNotSupportedException {
            return (pair) super.clone();
        }
    }


    public static class Tree{

        static Node root;


    }


        public static void treeWriter(String indexFile) throws IOException {

            BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile, false));
            //만약 degree:만 있는 상황이면 true
            bw.write("degree: "+D+"\n");
            treeWrite(Tree.root, bw, 0); // 0에서 인덱스 시작

            bw.write(leave);
            bw.write("\n");

            bw.flush();
            bw.close();
        }

        // treeWriter
        public static void treeWrite(Node node, BufferedWriter bw, int num) throws IOException { //num은 parent의 index num

            if (node == null) {
                System.out.println("저장할 노드가 없습니다.");
                System.exit(0);
            }

            int index = ++index_num;

            if (node instanceof LNode) { //root가 leaf node 일 경우
                LNode leaf = (LNode) node;
                bw.write("# " + num + " " + index + " "); //#  0 1
                for (int i = 0; i < leaf.p_.size(); i++) {
                    bw.write(leaf.p_.get(i).key + "," + leaf.p_.get(i).value + " ");
                }

                bw.write("\n");

                leave += index + ",";
                return;
            }

            nLNode nlNode = (nLNode) node;
            bw.write("# " + num + " " + index + " "); //#  0 1
            for (int i = 0; i < nlNode.p.size(); i++) {
                bw.write(nlNode.p.get(i).key + "," + nlNode.p.get(i).value + " ");
            }
            bw.write("\n");

            for (int i = 0; i < nlNode.p.size(); i++) {
                if (nlNode.p.get(i).l_child != null) {
                    treeWrite(nlNode.p.get(i).l_child, bw, index);
                }
            }

            if (nlNode.rm_childNode != null) {
                treeWrite(nlNode.rm_childNode, bw, index);
            }


        }

        public static void insertion(String indexFilen, String inputFilen) throws IOException, CloneNotSupportedException {
            readFile(indexFilen);

            try {
                File inputFile = new File("./" + inputFilen);
                BufferedReader br2 = new BufferedReader(new FileReader(inputFile));
                String line = "";

                while ((line = br2.readLine()) != null) { //line이 끝나기 전에
                    String[] arr2 = line.split(","); //arr 배열에 라인을 "," 기준으로 split
                    //key랑 value 가 배열에 담김
                    int key = Integer.parseInt(arr2[0]);
                    int value = Integer.parseInt(arr2[1]);
                    insert(key, value);

                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public static void insert(int key, int value) throws CloneNotSupportedException {
            /*
            root가 null인 상태라면 LNode형 객체를 만든 뒤 key, value를 넣어주고 root에 대입한다.
             */
            if (Tree.root == null) {
                LNode node = new LNode(key, value);
                Tree.root = node;
                return;
            }
            /*
            그 외에는 Node형 임시 노드에 root를 저장한 후
            1.임시 노드가 leaf라면 -> LNode 형 객체의 insert_key 함수
            2.임시 노드가 nonLeaf라면 -> nLNode형 객체의 insert_key 함수
             */
            Node temp = Tree.root;
            while (true) {

                if (!temp.notLeaf(temp)) { //leaf 일 경우
                    LNode lNode = (LNode) temp;
                    lNode.insert_key(key, value);
                    break;
                } else {

                    nLNode nLNode = (nLNode) temp;
                    nLNode.insert_key(key, value);
                    break;
                }
            }

        }


        public static void main(String[] args) throws IOException, CloneNotSupportedException {

            String ins = args[0];


            if (ins.equals("-c")) {
                createFile(args[1], Integer.parseInt(args[2]));
            }

            else if (ins.equals("-i")) { //-i index.dat input.csv

                String index = args[1];
                String input  = args[2];
                insertion(index, input);//file input 읽기

                treeWriter(args[1]); //index.dat


            } else if (args[0].equals("-s")) {
                singleKey_Search(args[1], Integer.parseInt(args[2]));
            }

            else if (args[0].equals("-r")) {
                rangedKey_Search(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            }

            else if (args[0].equals("-d")){}

        }


    }
