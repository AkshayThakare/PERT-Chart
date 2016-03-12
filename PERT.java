import java.util.LinkedList;
import java.util.Stack;

//import Graph.Vertex;
/**
 * The PERT class is used to calculate EC, LC, slack, critical paths and display the tasks information(EC, LC, slack)
 * @author  Akshay Thakare
 * @version 1.0, MAy 2015
 */
public class PERT {

	private static int criticalPathLength;
	private static int countofCriticalTasks;

	/**
	 * This method calculates the EC for each task(Vertex) using the topological ordering 
	 * @param topological order list
	 */
	public static void calculateEC(LinkedList<Graph.Vertex> topologicalList) {
		for (Graph.Vertex v : topologicalList) {
			if(v.name == 0){ // set EC = 0 for the start node
				v.EC = 0;
			}else{ // iterate over each of the vertices in predeccesor list and calculate the EC
				int maxEC = 0;
				for (Graph.Vertex u : v.predeccesorsList) {
					if(maxEC <= u.EC){
						maxEC = u.EC;
						v.EC = maxEC + v.duration;
					}
				}
			}
		}
	}

	/**
	 * This method calculates the LC for each task(Vertex) using the reverse topological ordering 
	 * @param revStack: reverse topological ordering, finishNode
	 */
	public static void calculateLC(Stack<Graph.Vertex> revStack, int finishNode) {
		criticalPathLength = revStack.peek().EC;
		//iterate over the reverse topological ordering 
		while(!revStack.isEmpty()){
			Graph.Vertex u = revStack.pop();
			//set LC for the finish node = critical path length
			if(u.name == (finishNode) || u.outdegree == 0){
				u.LC = criticalPathLength;
			}else{
				int minLC = Integer.MAX_VALUE;
				for (Graph.Edge e: u.Adj) {
					Graph.Vertex v = e.To;
					if(minLC > (v.LC-v.duration)){
						minLC = v.LC-v.duration;
						u.LC = minLC;
					}
				}
			}
		}
	}

	/**
	 * This method the slack for each each task(Vertex) of the graph
	 * @param g: Graph
	 */
	public static void calculateSlack(Graph g) {
		countofCriticalTasks = 0;
		//iterate over each vertex and calculate the slack
		for (Graph.Vertex u : g) {
			u.slack = u.LC - u.EC;
			if(u.slack == 0){ // calculate the total number of critical tasks
				countofCriticalTasks=countofCriticalTasks+1;
			}
		}
	}

	/**
	 * This method calculates the critical paths by traversing all paths using DFS
	 * @param current: start node, end: finish node
	 * @return critical path list: list of the all critical paths
	 */
	public static LinkedList<Stack<Graph.Vertex>> calculateCriticalPath(Graph.Vertex current,Graph.Vertex end) {
		Stack<Graph.Vertex> path = new Stack<Graph.Vertex>();
		LinkedList<Stack<Graph.Vertex>> cPathsList = new LinkedList<Stack<Graph.Vertex>>();
		allPathsDFS(path, cPathsList,current,end);
		return cPathsList;
	}

	/**
	 * This method finds all the possible critical paths using DFS algo
	 * @param path: A path from a source to vertex, cPathsList: List of critical paths
	 * @param current: current node, end: finish node
	 */
	private static void allPathsDFS(Stack<Graph.Vertex> path,LinkedList<Stack<Graph.Vertex>> cPathsList, Graph.Vertex current, Graph.Vertex end) {
		Stack<Graph.Vertex> revCPathStack = new Stack<Graph.Vertex>();
		Stack<Graph.Vertex> cPathStack = new Stack<Graph.Vertex>();
		//save the critical path if finish node is reached
		if(current == end){
			cPathStack = (Stack<Graph.Vertex>) path.clone();
			while(!revCPathStack.isEmpty()){
				cPathStack.push(revCPathStack.pop());
			}
			cPathsList.add(cPathStack);
		}else{
			if(current.slack == 0){// check if the current task is an critical task
				current.seen = true;
				path.add(current);
				for (Graph.Edge e : current.Adj) {
					Graph.Vertex v = e.To;
					if(current!=v && v.seen == false && v.slack == 0 && v.EC == current.EC + v.duration){
						//calls allPathsDFS for next node
						allPathsDFS(path, cPathsList,v,end);
					}
				}
				current.seen = false;
				path.pop();
			}
		}

	}
	/**
	 * This method displays all the possible critical paths
	 * @param Graph g
	 * @param cPathsList: critical path list
	 */
	public static void displayCriticalPath(Graph g,LinkedList<Stack<Graph.Vertex>> cPathsList) {
		System.out.println(criticalPathLength + " "+(countofCriticalTasks-1)+" "+cPathsList.size());
		System.out.println(" ");
		int countPaths = 0; // to count the number of critical paths
		for (Stack<Graph.Vertex> path : cPathsList) { // iterate over the critical paths list
			System.out.println();
			countPaths++;
			System.out.print(countPaths+" : ");
			if(!path.isEmpty()){
				path.pop();
			}
			while(!path.isEmpty()) {
				System.out.print(path.pop()+" ");
			}
		}
		System.out.println("\nTask\tEC\tLC\tSlack");
		//iterate over graph and print the tasks(vertices) information
		for (Graph.Vertex v : g) {
			if(v.name!=0){
				System.out.println(v+"\t"+v.EC+"\t"+v.LC+"\t"+v.slack);
			}

		}

	}

}
