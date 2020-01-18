# KD Tree
Constructed a KD-Tree using breadth first traversal that performs searching of nearest neighbors.

The principal idea of a kd-tree is to search the nearest neighbor of a query point in k-th dimension by traversing through the tree (similar to binary search tree). Each data point is stored inside a leaf node, which is by definition the outermost node of the search tree. 

My algorithm ignores the nearest points that are found on the other side of the splitting plane. For more information, consult (http://pointclouds.org/documentation/tutorials/kdtree_search.php). 


I will be testing if the tree is built correctly. Once built successfully, it will be tested on with 6 3-D datapoints. 

![](https://github.com/yi-fang-yuan/KD-Tree/blob/master/kdtree.png)
