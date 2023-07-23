from sklearn.cluster import KMeans
import numpy as np
import time
import pandas as pd
from matplotlib import pyplot

"""
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

print(f"Execution time: {str(execution_time)} ms")
print(f"Cluster centers:\n{str(kmeans.cluster_centers_)}")
print(f"Iterations: {str(kmeans.n_iter_)}")

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
pyplot.title(f'[{n_samples} samples][model comparison][cluster centers]')


mr_centroids = np.loadtxt(mapreduce_centroids_filename, delimiter=',')

# Perform K-means clustering using MapReduce implementation centroids
mr_kmeans = KMeans(n_clusters=k_clusters, init=mr_centroids, n_init=1)
mr_kmeans.fit(dataset)
mr_final_centroids = mr_kmeans.cluster_centers_



# Plot the final centroids of MapReduce implementation in green,magenta
pyplot.scatter(mr_final_centroids[:, 0], mr_final_centroids[:, 1], c='cyan', alpha=0.7, label='Final Centroids (MapReduce)')

# Plot the final centroids of Python implementation in red
pyplot.scatter(centr[:, 0], centr[:, 1], c='red', label='Final Centroids (Python)')

# Save and show the plot
plot_filename = f"plot_compare_models_{d_dimensions}D{k_clusters}K{n_samples}N.png"
pyplot.savefig(plot_filename)
pyplot.legend()
pyplot.show()

