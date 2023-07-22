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

## The Pseudocode(s) in the Project
Here after we will present the **pseudocode** that we used in the development of this project.
### Mapper Pseudocode
Input:
```java
key : the offset from the file 
value : datapoint p
```
Output: 
```java
key: µc i.e the closest centroid
value :p, i.e datapoint p.
```
```java
class MAPPER
	The initial centroids {µ1,µ2,...} are randomly sampled from X and are stored in a file.
	method MAP(offset_file,DataPoints p)
		// initialize values, min distance and the closest centroid
		minDistance <- +infinity
		centroid µc <- {}
		for each centroid µ in list_of_centroids[µ1,µ2,...] do
			// calculate distance
			distance <- calculateDistance(p,µ)
		 	// update the closest centroid
		 	if distance < minDistance:
		   	 minDistance = distance
       		   	 µc = centroid µ
	  EMIT(centroid µc,DataPoints p)
```
### Combiner Pseudocode
On every stage we need to sum the data points belonging to a cluster to calculate the centroid (arithmetic mean of points). 
Since the sum is an associative and commutative function, it will be very advantageous to use a combiner to reduce the amount 
of data to be transmitted to the reducers.

The Combiner algorithm takes as input a **centroid** and a **list of points in that centroid**. For all points in the list calculates the partial count as the sum of all the counts and the partial sum as the sum of all the points. At the end emits the centroid as the key and the list of partialSum as value.

Input
```java
key:centroid_index
value: list_of_points_in_centroid	
```
Output
```java
key: centroid_index
value: partial_point_sum
```
Here is the pseudocode of the Combiner
 ```java
class COMBINER
    method COMBINER(centroid_index, list_of_points)
        partial_points_sum = 0
        partial_points_count = 0
        for all point in list_of_points_in_ω:
            partial_points_sum  += point
            partial_points_count += 1
        EMIT(centroid_index, partial_points_sum) 
```

### Reducer Pseudocode
Finally the **reducer** calculates the new approximation of the centroid and emits it. 

**Input**
```java
key: centroid_index
value:list_partial_points_sum
```
**Output**
```java
key: new_centroid_index
value:new_centroid
```
Here is the full pseudocode for the Reducer.
```java
class REDUCER
	method REDUCE(centroid_index, list_partial_points_sum)
		total_count = getNumPoints(partial_points_sum)
		points_sum=0
		for all partial_points_sum in list_partial_points_sum:
			points_sum += partial_point_sum
			total_count +=getNumPoints(partial_points_sum)
		new_centroid_=points_sum/total_count
	EMIT(centroid_index,new_centroid)
```**
NB:
The result of the MapReduce stage will be the same even if the combiner is not called by the Hadoop framework.

###  Validation of Our Algorithm
#### 2D random sample dataset
We generated a sample dataset using a python script to validate the 
work we did using **Mapreduce** and the **kmeans using python's** kmeans
class.

## Test
 
-





