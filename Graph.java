import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

/**
 * The Graph class is used to create graph, its topological order and calls method from PERT class to calculate the critical paths
 * @author  Akshay Thakare
 * @version 1.0, MaY 2015
 */
public class Graph implements Iterable<Graph.Vertex> {
	static final int INFINITY = Integer.MAX_VALUE;
	public Vertex[] V; // array of vertices
	public int N; // number of vertices in the graph
	private int phase = 0;
	private long startTime, endTime, elapsedTime;
	/**
	 * Constructor for Graph
	 * 
	 * @param size
	 *            : int - number of vertices
	 */
	Graph(int size) {
		N = size;
		V = new Vertex[size + 1];
		// create an array of Vertex objects
		for (int i = 0; i <= size; i++)
			V[i] = new Vertex(i);
	}

	public void timer()
	{
		if(phase == 0) {
			startTime = System.currentTimeMillis();
			phase = 1;
		} else {
			endTime = System.currentTimeMillis();
			elapsedTime = endTime-startTime;
			System.out.println("Time: " + elapsedTime + " msec.");
			memory();
			phase = 0;
		}
	}

	public void memory() {
		long memAvailable = Runtime.getRuntime().totalMemory();
		long memUsed = memAvailable - Runtime.getRuntime().freeMemory();
		System.out.println("Memory: " + memUsed/1000000 + " MB / " + memAvailable/1000000 + " MB.");
	}
	/**
	 * Class that represents an arc in a Graph
	 *
	 */
	public class Edge {
		public Vertex From; // head vertex
		public Vertex To; // tail vertex

		/**
		 * Constructor for Edge
		 * 
		 * @param u
		 *            : Vertex - The head of the arc
		 * @param v
		 *            : Vertex - The tail of the arc
		 * @param w
		 *            : int - The weight associated with the arc
		 */
		Edge(Vertex u, Vertex v) {
			From = u;
			To = v;
		}

		/**
		 * Method to find the other end end of the arc given a vertex reference
		 * 
		 * @param u
		 *            : Vertex
		 * @return
		 */
		public Vertex otherEnd(Vertex u) {
			// if the vertex u is the head of the arc, then return the tail else return the head
			if (From == u) {
				return To;
			} else {
				return From;
			}
		}

		/**
		 * Method to represent the edge in the form (x,y) where x is the head of
		 * the arc and y is the tail of the arc
		 */
		public String toString() {
			return "(" + From + "," + To + ")";
		}
	}

	/**
	 * Class to represent a vertex of a graph
	 * 
	 *
	 */
	public class Vertex implements Comparable<Vertex> {
		public int name; // name of the vertex
		public boolean seen; // flag to check if the vertex has already been visited
		public Vertex parent; // parent of the vertex
		public int weight; // field for storing int attribute of vertex
		public LinkedList<Edge> Adj; // adjacency list
		public int indegree; // indegree of the node
		public int outdegree;// outdegree of the node
		public boolean active; //active flag
		public int duration; // duration required for task
		public LinkedList<Vertex> predeccesorsList;
		public int EC; // earliest completion time when task is completed
		public int LC; // latest completion time when task is completed
		public int slack; // slack



		/**
		 * Constructor for the vertex
		 * 
		 * @param n
		 *            : int - name of the vertex
		 */
		Vertex(int n) {
			name = n;
			seen = false;
			parent = null;
			active=false;
			indegree=0;
			predeccesorsList = new LinkedList<Vertex>();
			EC=0;
			LC=0;
			slack=0;
			Adj = new LinkedList<Edge>();
			duration = 0;
			outdegree = 0; 
		}


		/**
		 * Method used to order vertices, based on algorithm
		 */
		public int compareTo(Vertex v) {
			return this.weight - v.weight;
		}

		/**
		 * Method to represent a vertex by its name
		 */
		public String toString() {
			return Integer.toString(name);
		}
	}


	/**
	 * Method to add an arc to the graph
	 * 
	 * @param a
	 *            : int - the head of the arc
	 * @param b
	 *            : int - the tail of the arc
	 * 
	 */
	void addEdge(int a, int b) {
		Edge e = new Edge(V[a], V[b]);
		V[a].Adj.add(e);
		V[b].predeccesorsList.add(V[a]);
		V[a].outdegree++;
		V[b].indegree++;
	}

	/**
	 * Method to create an instance of VertexIterator
	 */
	public Iterator<Vertex> iterator() {
		return new VertexIterator<Vertex>(V, N);
	}

	/**
	 * A Custom Iterator Class for iterating through the vertices in a graph
	 * 
	 *
	 * @param <Vertex>
	 */
	@SuppressWarnings("hiding")
	private class VertexIterator<Vertex> implements Iterator<Vertex> {
		private int nodeIndex = 0;
		private Vertex[] iterV;// array of vertices to iterate through
		private int iterN; // size of the array

		/**
		 * Constructor for VertexIterator
		 * 
		 * @param v
		 *            : Array of vertices
		 * @param n
		 *            : int - Size of the graph
		 */
		private VertexIterator(Vertex[] v, int n) {
			nodeIndex = 0;
			iterV = v;
			iterN = n;
		}

		/**
		 * Method to check if there is any vertex left in the iteration
		 * Overrides the default hasNext() method of Iterator Class
		 */
		public boolean hasNext() {
			return nodeIndex != iterN;
		}

		/**
		 * Method to return the next Vertex object in the iteration
		 * Overrides the default next() method of Iterator Class
		 */
		public Vertex next() {
			Vertex v = iterV[nodeIndex];
			nodeIndex++;
			return v;
		}

		/**
		 * Throws an error if a vertex is attempted to be removed
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in;
		if (args.length > 0) {
			File inputFile = new File(args[0]);
			in = new Scanner(inputFile);
		} else {
			in = new Scanner(System.in);
		}
		Graph g = readGraph(in);
		//g.printGraph();
		// start timer
		g.timer();
		//Stack of Vertex in topological order
		Stack<Vertex> stackVertex = new Stack<Vertex>();
		//calculate topological ordering
		stackVertex = g.toplogicalOrder2(g);
		//calculate topological ordering
		LinkedList<Vertex> topologicalList = new LinkedList<Graph.Vertex>(); // for calculating the EC
		//iterate over the stack and add elements to this List
		while(!stackVertex.isEmpty()){
			topologicalList.add(stackVertex.pop());
		}
		//create a reverse stack of Vertex for calculating the LC
		Stack<Vertex> revStack = new Stack<Vertex>();
		for(Vertex v: topologicalList){
			revStack.push(v);
		}
		//calculate EC
		PERT.calculateEC(topologicalList);
		//calculate LC
		PERT.calculateLC(revStack,g.N);
		//calculate slack
		PERT.calculateSlack(g);
		//reinitialize the seen flags
		g.initialize();
		//calculate the critical paths
		LinkedList<Stack<Vertex>> cPathsList = PERT.calculateCriticalPath(g.V[0],g.V[g.N]);
		//display the critical paths
		PERT.displayCriticalPath(g, cPathsList);
		//stop the timer
		g.timer();
	}

	static Graph readGraph(Scanner in) {
		// read the graph related parameters
		int n=0;// number of vertices in the graph
		int m=0;// number of edges in the graph
		String input = in.nextLine();
		if(!input.startsWith("#")){
			String inputToken[] = input.split(" ");
			if(inputToken.length==2){
				n = Integer.parseInt(inputToken[0]);
				m = Integer.parseInt(inputToken[1]);
			}
		}else{
			n = in.nextInt();
			m = in.nextInt(); 
		}


		// create a graph instance
		Graph g = new Graph(n+1);

		//Add start node
		int start=0;
		//Add finish node
		int finish=g.N;

		// set duration for each vertex
		for(Vertex v : g){
			if(v.name==start || v.name==finish){
				v.duration=0;
			}else{
				int duration = in.nextInt();
				v.duration=duration;
			}
		}

		for(int i = 0; i < m; i++) {
			int u = in.nextInt();
			int v = in.nextInt();
			g.addEdge(u, v);
		}

		/*Add edges from start node to every node with indegree '0' and edge from all nodes with outdegree 0 to finish node*/
		for(Vertex v : g) {
			if(v.indegree == 0 && v.name!=start && v.name!=finish){
				g.addEdge(start, v.name);
				g.V[start].outdegree++;
			}
			if(v.outdegree == 0 && v.name!=start && v.name!=finish){
				g.addEdge(v.name, finish);
				g.V[finish].indegree++;
			}
		}
		in.close();
		return g;
	}

	/**
	 * Method to initialize a graph
	 *  1) Sets the seen attribute of every vertex as false 
	 * 
	 * @param g
	 *            : Graph - The reference to the graph
	 */
	void initialize() {
		for (Vertex u : this) {
			u.seen = false;
			this.V[this.N].seen = false;
		}
	}

	/**
	 * Method to print the graph
	 * 
	 * @param g
	 *            : Graph - The reference to the graph
	 */
	void printGraph() {
		for (Vertex u : this) {
			System.out.print(u + ": ");
			for(Edge e: u.Adj) {
				System.out.print(e);
			}
			System.out.println();
		}
	}

	/**
	 * Method that gives the topological order for the given graph
	 * 
	 * @param g : Graph - The reference to the graph
	 * @return : Stack of Vertex
	 */
	private Stack<Vertex> toplogicalOrder2(Graph g) {
		//set all active and seen flag of all vertices to false
		for (Vertex u : this) {
			u.seen=false;
			u.active=false;
		}
		Stack<Vertex> stackVertex = new Stack<Vertex>();
		//call DFSVisit on each unseen Vertex
		for (Vertex u : this) {
			if(!u.seen){
				DFSVisit(u,stackVertex);
			}
		}
		return stackVertex;
	}

	private void DFSVisit(Vertex u, Stack<Vertex> stackVertex) {
		u.seen=true;// set u as seen
		u.active=true;// set u as active
		/*for each vertex adjacent to u check if it is seen*/
		for(Edge e: u.Adj) {
			if(!e.To.seen){// if it is not seen call DFSVisit for that adjacent vertex
				DFSVisit(e.To,stackVertex);
			}else if(e.To.active){// if the adjacent vertex is already active then throw exception
				try {
					throw new Exception("Not a DAG");
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}
			}
		}
		//push u on stack
		stackVertex.push(u);
		u.active=false; //set u as not active
	}
}