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

- d_dimensions=2
- k_clusters = 2
- n_samples=500

### Dataset generator script
The python script [here](https://github.com/LijTesfaye/kmeans_clustering_toretto_group/blob/master/python-module/dataset_Gen.py) is used to generate a madeup dataset.
``` python
"""
main imports goes here 
"""
#
# basic variables of the kmeans
d_dimensions = 2
k_clusters = 2
n_samples =500

# the decimal places
decimal_places = 4
"""
Writing  the generated data to the file.
"""
X_data, y = make_blobs(n_samples=n_samples, n_features=d_dimensions, centers=k_clusters,cluster_std=1.2, random_state=42)
filename = f"data{d_dimensions}D{k_clusters}K{n_samples}N.txt"
.... [Code Cropped ] ...
```
## Initial centroids generator
Once the **dataset** is generated the logical step is to generate the **intial centroids**.
The script in [here](https://github.com/LijTesfaye/kmeans_clustering_toretto_group/blob/master/python-module/iCentroidsGen.py) does the job.
Let's have a taste of it ...
```python
import numpy as np
# Example usage
d_dimensions=2
k_clusters = 2
n_samples=500
#
data_file = f"data{d_dimensions}D{k_clusters}K{n_samples}N.txt"
output_file = f"icRandom_{d_dimensions}D{k_clusters}K{n_samples}N.txt"

data = np.loadtxt(data_file, delimiter=',')  # Load data from the text file
num_dimensions = data.shape[1]
print("Number of dimensions:", num_dimensions)
# Generate random initial centroids
np.random.seed()  # Use system time as the random seed
centroids = np.random.permutation(data)[:k_clusters]

# Write initial centroids to the output file with four decimal places
np.savetxt(output_file, centroids, delimiter=',', fmt='%.4f')
print(f"Initial centroids saved to {output_file}")
```
## The kmeans implementation in Python
The third logocal step is to design a python implementation of the kmeans algorithm  for the validation purpose.
This script is **very useful** to compare the **mapreduce** implementation of the **kmeans algorithm** to the one in the **python**.
The script is found [here](https://github.com/LijTesfaye/kmeans_clustering_toretto_group/blob/master/python-module/KmeansPythonModel.py).

```python
"""
imports goes here
"""
d_dimensions=2
k_clusters = 2
n_samples=500
#
test_case =1
# Load the initial centroids from the file
inputfile = f"icKmeansPP_{d_dimensions}D{k_clusters}K{n_samples}N.txt"
outputfile = f"fcKmeansPP_{d_dimensions}D{k_clusters}K{n_samples}N.txt"
initial_centroids = np.loadtxt(inputfile, delimiter=',')
points = []
###
max_iteration =100
#
start_milli_time = round(time.time() * 1000, 4)
filename = f"data{d_dimensions}D{k_clusters}K{n_samples}N.txt"
with open(filename, "r") as file:
    for line in file:
        comps = line.split(",")
        point = [float(comps[i]) for i in range(len(comps))]
        points.append(point)
dataset = np.array(points)
kmeans = KMeans(n_clusters=k_clusters,
                init=initial_centroids,
                max_iter=max_iteration,
                random_state=42).fit(dataset)
end_milli_time = round(time.time() * 1000, 4)
execution_time = round(end_milli_time - start_milli_time, 4)
...[Code cropped] ...
```
## Compare and Plot
This python script takes as input the final centroids generated by the **mapreduce** and the  **python** implementation of the correponding use-case and plots them 
to a single 2d graph so that we can compare the results of the two implementations. The script is named as **compare_models.py** and found inside the python module [here](https://github.com/LijTesfaye/kmeans_clustering_toretto_group/tree/master/python-module ).
```python
"""
imports goes here
"""
d_dimensions = 2
k_clusters = 2
n_samples = 500

test_case = 1

# Load the initial centroids from the file
python_centroids_filename = f"icRandom_{d_dimensions}D{k_clusters}K{n_samples}N.txt"
outputfile = f"fcRandom_{d_dimensions}D{k_clusters}K{n_samples}N.txt"
# Load final centroids from MapReduce implementation
mapreduce_centroids_filename = f'fcMapReduce_{d_dimensions}D{k_clusters}K{n_samples}N.txt'

initial_centroids = np.loadtxt(python_centroids_filename, delimiter=',')
points = []

max_iteration = 100

start_milli_time = round(time.time() * 1000, 4)
filename = f"data{d_dimensions}D{k_clusters}K{n_samples}N.txt"
with open(filename, "r") as file:
    for line in file:
        comps = line.split(",")
        point = [float(comps[i]) for i in range(len(comps))]
        points.append(point)
dataset = np.array(points)
kmeans = KMeans(n_clusters=k_clusters,
                init=initial_centroids,
                max_iter=max_iteration,
                random_state=42).fit(dataset)
end_milli_time = round(time.time() * 1000, 4)
execution_time = round(end_milli_time - start_milli_time, 4)
# Write the final centroids to a file
with open(outputfile, "w") as output_file:
    output_file.write(f"Execution time: {execution_time} ms\n")
    output_file.write(f"Cluster centers:\n")
    for centroid in kmeans.cluster_centers_:
        rounded_centroid = np.around(centroid, decimals=4)
        output_file.write(','.join(map(str, rounded_centroid)) + '\n')
    output_file.write(f"Number of iterations: {kmeans.n_iter_}")

# Plot the points as blue and the cluster centers as red
cent = kmeans.cluster_centers_
centr = np.array(cent)
# Concatenate the points with the centroids
c = np.vstack([dataset, centr])

df = pd.DataFrame({'x': c[:, 0], 'y': c[:, 1]})
# Assign labels for points and centroids
labels = ['blue'] * len(dataset) + ['red'] * k_clusters
df['label'] = labels

# Plot the dataset points and initial centroids
df.plot(x='x', y='y', c=df['label'], kind='scatter')
pyplot.xlabel(f'({d_dimensions} dimensions)')
pyplot.ylabel(f' ({k_clusters} clusters)')
pyplot.title(f'[Compare Models][{n_samples} samples][model comparison][cluster centers]')


mr_centroids = np.loadtxt(mapreduce_centroids_filename, delimiter=',')

# Perform K-means clustering using MapReduce implementation centroids
mr_kmeans = KMeans(n_clusters=k_clusters, init=mr_centroids, n_init=1)
mr_kmeans.fit(dataset)
mr_final_centroids = mr_kmeans.cluster_centers_

...[Code cropped] ...
```
Sample result for test-case1.It is plotted using the **compare_models.py** script. The centers of the two models are overlapped and you can see only one color.
![Test-case1](https://github.com/LijTesfaye/kmeans_clustering_toretto_group/blob/master/python-module/plot_compare_models_2D2K500N.png).

# Test Cases
We did 8(User generated)+1(Iris dataset i.e a real data) types of test cases.
If you are interested in those test case you can check them in the directory found [here](https://github.com/LijTesfaye/kmeans_clustering_toretto_group/tree/master/python-module)
# Community
- [Tesfaye](https://github.com/LijTesfaye)
- [Aida](https://github.com/aidahimm/)
- [Michael](https://github.com/Mickey374)
