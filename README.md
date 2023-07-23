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
key:the closest centroidID
value :point,i.e datapoint p.
```
```java
Class MAPPER
  method Map(key, point)
    centroidID <- null
    distanceFromCentroid <- Infinity
    for every centroid in centroidsList do
      distance <- Distance(centroid, point)
      if centroidID = null || distance < distanceFromCentroid then
        centroidID <- centroid.CentroidID
        distanceFromCentroid <- distance
  Emit(centroidID, point)
```
Initial centroids are read from the Hadoop configration using the setup() method.
```java
method setup()
  initCentroids <-readCentroidsConf(context)
```
### Combiner Pseudocode
On every stage we need to sum the data points belonging to a cluster to calculate the centroid (arithmetic mean of points). 
Since the sum is an associative and commutative function, it will be very advantageous to use a combiner to reduce the amount 
of data to be transmitted to the reducers.

The Combiner algorithm takes as input a **centroid** and a **list of points in that centroid**. For all points in the list calculates the partial count as the sum of all the counts and the partial sum as the sum of all the points. At the end emits the centroid as the key and the list of partialSum as value.

Input
```java
key:centroidId
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
  method Reduce(centroidId, points)
    partialSum <- sum(points)
    Emit(centroidId, partialSum)
```
### Reducer Pseudocode
Finally the **reducer** calculates the new approximation of the centroid and emits it. 

**Input**
```java
key: centroidId
value:list_partial_points_sum
```
**Output**
```java
key: new_centroidID
value:nextCentroidPoint
```
Here is the full pseudocode for the Reducer.
```java
class REDUCER
  method Reduce(centroidId, partialSums)
    finalSum <- sum(partialSums)
    nextCentroidPoint <- finalSum.average()
  Emit(centroidId, nextCentroidPoint)
```
NB:
The result of the MapReduce stage will be the same even if the combiner is not called by the Hadoop framework.

## Python for Validation 
For the purpose of validation of our work we used python. First lets have a little description of the main python scripts that are used in this project.
In every python script the user is expected to change these three variables based on the use-case they are working on.
The one below shows that we are going to work on a datset that has 4-dimension and number of clusters are set to 4 and the number of samples is set to 500.

**Here are the values**

- d_dimensions=4
- k_clusters = 4
- n_samples=500

### Dataset generator script
The python script [here](https://github.com/LijTesfaye/kmeans_clustering_toretto_group/blob/master/python-module/dataset_Gen.py) is used to generate a madeup dataset.
### 
### 2D random sample dataset

We generated a sample dataset using a python script to validate the 
work we did using **Mapreduce** and the **kmeans using python's** kmeans
class.

### Test Cases
-





