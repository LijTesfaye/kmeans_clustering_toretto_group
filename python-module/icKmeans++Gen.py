import numpy as np

def kmeans_plus_plus(X, n_clusters):
    # Randomly select the first centroid
    centroids = [X[np.random.randint(X.shape[0])]]

    # Calculate the squared Euclidean distances from each point to the nearest centroid
    distances = np.sum((X - centroids[0]) ** 2, axis=1)

    # Select the remaining centroids iteratively
    for _ in range(1, n_clusters):
        # Choose the next centroid based on the calculated probabilities
        probabilities = distances / np.sum(distances)
        next_centroid_index = np.random.choice(X.shape[0], p=probabilities)
        next_centroid = X[next_centroid_index]
        centroids.append(next_centroid)
        # Update the squared Euclidean distances for the new centroid
        new_distances = np.sum((X - next_centroid) ** 2, axis=1)
        distances = np.minimum(distances, new_distances)
    return np.array(centroids)
#
# change the values of d_dimensions,k_clusters,n_samples based on your use case.
d_dimensions = 4
k_clusters = 4
n_samples = 150

data_file = f"data{d_dimensions}D{k_clusters}K{n_samples}N.txt"
output_file = f"icKmeansPP_{d_dimensions}D{k_clusters}K{n_samples}N.txt"

data = np.loadtxt(data_file, delimiter=',')  # Load data from the text file

num_dimensions = data.shape[1]
print("Number of dimensions:", num_dimensions)

# initial Centroids generation
initial_centroids = kmeans_plus_plus(data, n_clusters=k_clusters)

# Write initial centroids to the output file with four decimal places
np.savetxt(output_file, initial_centroids, delimiter=',', fmt='%.4f')
print(f"Initial centroids saved to {output_file}")


