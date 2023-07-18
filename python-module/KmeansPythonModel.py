from sklearn.cluster import KMeans
import numpy as np
import time
import pandas as pd
from matplotlib import pyplot
""""
This file does the following  major things
1) It generates the centers of every claster 
2) plots the center of each cluster in red and the rest of the cluster in blue
3) saves the plot
"""
d_dimensions=4
k_clusters = 4
n_samples=150
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
print(df)
#
df.plot(x='x', y='y', c=df['label'], kind='scatter')
pyplot.legend()
pyplot.xlabel(f'({d_dimensions} dimensions)')
pyplot.ylabel(f' ({k_clusters} clusters)')
pyplot.title(f'[Madeup dataset][KmeansPP][{n_samples} samples][cluster centers]')
plot_filename = f"plot_Cluster_Centers_KmeansPP_{d_dimensions}D{k_clusters}K{n_samples}N.png"
pyplot.savefig(plot_filename)
pyplot.show()
