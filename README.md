# K Means Clustering Using Hadoop's MapReduce
This is a semester project for the Cloud Computing course at the University of Pisa,Italy.
## Brief description of the Kmeans Algorithm
K-means clustering is one of the popular unsupervised machine learning algorithms.It is employed to classify a given data set through a 
certain number of clusters fixed a priori.
Here is the main idea 

***The main idea is to define *k* centroids aka means, one for each cluster, and taking each point from the given data set to associate it to the nearest centroid. When no point is pending, the next step is to re-calculate k new centroids as barycenters of the previous resulting clusters. With these k new centroids, a new binding has to be done between the same data set points and the nearest new centroid generating a loop in which the k centroids change their location step by step until no more changes are done. Finally, this algorithm aims at minimizing the squared error function of the distance measure between a data point and the cluster centre as an indicator of the distance of the *n* data points from their respective cluster centres.***

# The MapReduce Framework Programming Model
These are the *four* main steps that the mapreduce framework process goes through.
- **Splitting**: The large input data will be split into fixed size chunks(blocks) that will be consumed by a single mapp.
- **Mapping**: The data in every split goes to a mapping function to produce as output *(key-value)* pairs.
- **Shuffling**: This phase consumes the output of Mapping phase. Its task is to consolidate the relevant records from Mapping phase output. 
- **Reducing**: In this phase, output values from the Shuffling phase are aggregated, in parallel. This phase combines values from Shuffling phase and returns a single output value. If more than one **Reducers** are used, the MapReduce system collects all the Reduce output and sorts it by key to produce the final outcome.

## The Hadoop Framework
Here after we will present the **pseudocode** that we used in the development of this project.
### Mapper Pseudocode
Input: a single datapoint **p** and **its offset from the file**.
Output: µi,p and count 1 where µi is the nearest centroid to the input datapoin,p, and the count.
```java
class MAPPER
	The list_of_centroids {µ1,µ2,...} are randomly sampled from X
	method MAP(offset_file,DataPoints p)
		// initialize values, min distance and the closest centroid
		minDistance <- +infinity
		centroid µ<sub>c</sub> <- {}
		for each centroid µ in list_of_centroids[µ1,µ2,...] do
      // calculate distance
			distance <- calculateDistance(p,µ)
			// update the closest centroid
			if distance < minDistance:
       minDistance = distance
       closestCentroid= centroid µ
	  EMIT(centroid closestCentroid,DataPoints p)
```
### Combiner Pseudocode



### Reducer Pseudocode




