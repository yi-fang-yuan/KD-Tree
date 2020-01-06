
import java.util.ArrayList;
import java.util.Iterator;

public class KDTree implements Iterable<Datum>{ 

	KDNode 		rootNode;
	int    		k; 
	int			numLeaves;
	
	// constructor

	public KDTree(ArrayList<Datum> datalist) throws Exception {

		Datum[]  dataListArray  = new Datum[ datalist.size() ]; 

		if (datalist.size() == 0) {
			throw new Exception("Trying to create a KD tree with no data");
		}
		else
			this.k = datalist.get(0).x.length;

		int ct=0;
		for (Datum d :  datalist) {
			dataListArray[ct] = datalist.get(ct);
			ct++;
		}
		
	//   Construct a KDNode that is the root node of the KDTree.

		rootNode = new KDNode(dataListArray);
	}
	
	//   KDTree methods
	
	public Datum nearestPoint(Datum queryPoint) {
		return rootNode.nearestPointInNode(queryPoint);
	}
	

	public int height() {
		return this.rootNode.height();	
	}

	public int countNodes() {
		return this.rootNode.countNodes();	
	}
	
	public int size() {
		return this.numLeaves;	
	}

	//-------------------  helper methods for KDTree   ------------------------------

	public static long distSquared(Datum d1, Datum d2) {

		long result = 0;
		for (int dim = 0; dim < d1.x.length; dim++) {
			result +=  (d1.x[dim] - d2.x[dim])*((long) (d1.x[dim] - d2.x[dim]));
		}
		// if the Datum coordinate values are large then we can easily exceed the limit of 'int'.
		return result;
	}

	public double meanDepth(){
		int[] sumdepths_numLeaves =  this.rootNode.sumDepths_numLeaves();
		return 1.0 * sumdepths_numLeaves[0] / sumdepths_numLeaves[1];
	}

	class KDNode {

		boolean leaf;
		Datum leafDatum;           //  only stores Datum if this is a leaf
		
		//  the next two variables are only defined if node is not a leaf

		int splitDim;      // the dimension we will split on
		int splitValue;    // datum is in low if value in splitDim <= splitValue, and high if value in splitDim > splitValue

		KDNode lowChild, highChild;   //  the low and high child of a particular node (null if leaf)
		  //  You may think of them as "left" and "right" instead of "low" and "high", respectively

		KDNode(Datum[] datalist) throws Exception{

			/*
			 *  This method takes in an array of Datum and returns 
			 *  the calling KDNode object as the root of a sub-tree containing  
			 *  the above fields.
			 */

			//   ADD YOUR CODE BELOW HERE
			ArrayList<Integer> james = new ArrayList<Integer>();
			ArrayList<Integer> jeanne = new ArrayList<Integer>();
			int index = 0;

			if (datalist.length>1){
				leaf=false;
			}
			if (datalist.length==1){
				leafDatum=datalist[0];
				leaf=true;
				numLeaves++;
			}
			else {
				for (int dim = 0; dim < k; dim++) {
					// Use constants
					//Integer.MIN_VALUE;
					//Integer.MAX_VALUE;
					int maxValue = Integer.MIN_VALUE;
					for (int i = 0; i < datalist.length; i++) {
						if (datalist[i].x[dim] > maxValue) {
							maxValue = datalist[i].x[dim];
						}
					}
					int minValue = Integer.MAX_VALUE;
					for (int i = 0; i < datalist.length; i++) {
						if (datalist[i].x[dim] < minValue) {
							minValue = datalist[i].x[dim];
						}
					}
					james.add(dim, maxValue - minValue);
					jeanne.add(dim, maxValue + minValue);
				}
				int max = Integer.MIN_VALUE;
				// use constant
				for (int i = 0; i < james.size(); i++) {
					if (james.get(i) > max) {
						max = james.get(i);
						index = i;
					}

				}

				double mean=jeanne.get(index);
				int avg=(int)Math.floor(mean/2);
				if (max == 0) {
					leaf = true;
					leafDatum = datalist[0];
					numLeaves++;
				}
				else {
					splitDim = index;
					splitValue = avg;

					ArrayList<Datum> datalist2 = new ArrayList<>();
					ArrayList<Datum> datalist3 = new ArrayList<>();
					for (int i = 0; i < datalist.length; i++) {
						if (datalist[i].x[splitDim] <= splitValue) {
							datalist2.add(datalist[i]);
						} else {
							datalist3.add(datalist[i]);
						}
					}
					Datum[] datalist2Array = new Datum[datalist2.size()];
					Datum[] datalist3Array = new Datum[datalist3.size()];
					for (int i = 0; i < datalist2.size(); i++) {
						datalist2Array[i] = datalist2.get(i);
					}
					for (int i = 0; i < datalist3.size(); i++) {
						datalist3Array[i] = datalist3.get(i);
					}
					this.lowChild = new KDNode(datalist2Array);
					this.highChild = new KDNode(datalist3Array);
				}
			}
			//   ADD YOUR CODE ABOVE HERE

		}

		public Datum nearestPointInNode(Datum queryPoint) {
			Datum nearestPoint, nearestPoint_otherSide;
			//   ADD YOUR CODE BELOW HERE
			double d1=0;
			double d2=0;
			double query_to_npo=0;
			if (leaf){
				return leafDatum;
			}
			else if (queryPoint.x[splitDim]>splitValue) {
				nearestPoint = highChild.nearestPointInNode(queryPoint);
				d1 = distSquared(queryPoint, nearestPoint);
				d2 = Math.abs(splitValue - queryPoint.x[splitDim]);
				if (d1 > d2) {
					nearestPoint_otherSide = lowChild.nearestPointInNode(queryPoint);
					query_to_npo = distSquared(queryPoint, nearestPoint_otherSide);
					if (d1 > query_to_npo) {
						return nearestPoint_otherSide;
					}

				}
			}
			else{
				nearestPoint=lowChild.nearestPointInNode(queryPoint);
				d1=distSquared(queryPoint,nearestPoint);
				d2=Math.abs(splitValue-queryPoint.x[splitDim]);
				if (d1>d2){
					nearestPoint_otherSide=highChild.nearestPointInNode(queryPoint);
					query_to_npo=distSquared(queryPoint,nearestPoint_otherSide);
					if (d1>query_to_npo){
						return nearestPoint_otherSide;
					}
				}
				 // calculate distace bet np and qp
			}

				return nearestPoint;
			//   ADD YOUR CODE ABOVE HERE
		}
		
		// -----------------  KDNode helper methods (might be useful for debugging) -------------------

		public int height() {
			if (this.leaf) 	
				return 0;
			else {
				return 1 + Math.max( this.lowChild.height(), this.highChild.height());
			}
		}

		public int countNodes() {
			if (this.leaf)
				return 1;
			else
				return 1 + this.lowChild.countNodes() + this.highChild.countNodes();
		}
		
		/*  
		 * Returns a 2D array of ints.  The first element is the sum of the depths of leaves
		 * of the subtree rooted at this KDNode.   The second element is the number of leaves
		 * this subtree.    Hence,  I call the variables  sumDepth_size_*  where sumDepth refers
		 * to element 0 and size refers to element 1.
		 */
				
		public int[] sumDepths_numLeaves(){
			int[] sumDepths_numLeaves_low, sumDepths_numLeaves_high;
			int[] return_sumDepths_numLeaves = new int[2];
			
			/*     
			 *  The sum of the depths of the leaves is the sum of the depth of the leaves of the subtrees, 
			 *  plus the number of leaves (size) since each leaf defines a path and the depth of each leaf 
			 *  is one greater than the depth of each leaf in the subtree.
			 */
			
			if (this.leaf) {  // base case
				return_sumDepths_numLeaves[0] = 0;
				return_sumDepths_numLeaves[1] = 1;
			}
			else {
				sumDepths_numLeaves_low  = this.lowChild.sumDepths_numLeaves();
				sumDepths_numLeaves_high = this.highChild.sumDepths_numLeaves();
				return_sumDepths_numLeaves[0] = sumDepths_numLeaves_low[0] + sumDepths_numLeaves_high[0] + sumDepths_numLeaves_low[1] + sumDepths_numLeaves_high[1];
				return_sumDepths_numLeaves[1] = sumDepths_numLeaves_low[1] + sumDepths_numLeaves_high[1];
			}	
			return return_sumDepths_numLeaves;
		}
		
	}

	public Iterator<Datum> iterator() {
		return new KDTreeIterator();
	}

	private class KDTreeIterator implements Iterator<Datum> {
		
		//   ADD YOUR CODE BELOW HERE
		ArrayList<Datum> datalist=new ArrayList<>();
		Datum tmp;
		int leaf_index=0;

		private KDTreeIterator(){
			inorderT(rootNode);
		}
		private void inorderT(KDNode rootNode){
			if(rootNode.leaf){
				datalist.add(rootNode.leafDatum);
			}
			else{
				inorderT(rootNode.lowChild);
				inorderT(rootNode.highChild);
			}
		}

		public boolean hasNext(){
			if (leaf_index < datalist.size()){
				return true;
			}
			else {
				return false;
			}
		}
		public Datum next(){
			tmp=datalist.get(leaf_index);
			leaf_index++;
			return tmp;
		}


		//   ADD YOUR CODE ABOVE HERE

	}


}

