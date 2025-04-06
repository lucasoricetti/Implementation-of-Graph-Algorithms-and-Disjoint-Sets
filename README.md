# Algorithm and Data Structure: Implementation of Adjacency Matrix, Forest Disjoint Sets, and Kruskal's Algorithm

## Description

This repository contains the implementation of the second project for the **Algorithms and Data Structures Lab** course (2024/2025) at the **University of Camerino**, under the guidance of Professor **Luca Tesei**. The project focuses on implementing key data structures and algorithms related to graphs and disjoint sets.

## Project Tasks and Implementation Details
1. **Adjacency Matrix Undirected Graph**: Implement the `AdjacencyMatrixUndirectedGraph<L>` class to represent an undirected graph using a modified adjacency matrix. Instead of boolean values, the matrix stores `GraphEdge<L>` objects to efficiently represent edge weights. Nodes are indexed by insertion order, and the matrix dynamically resizes as nodes are added or removed.

2. **Forest Disjoint Sets**: Implement the `ForestDisjointSets` class to manage disjoint sets using a forest of trees. Each tree represents a set, with nodes pointing to their parents and the root acting as the set representative. Optimizations like union by rank and path compression ensure near-constant time complexity for union and findSet operations.

3. **Connected Components**: Develop an algorithm to compute the connected components of an undirected graph, leveraging the ForestDisjointSets class for efficient set management.

4. **Kruskal's Algorithm**: Implement Kruskal's algorithm to find the Minimum Spanning Tree (MST) of a weighted undirected graph. The algorithm sorts edges by weight and uses ForestDisjointSets to detect cycles, ensuring the resulting tree is minimal.

## Dependencies
The project uses only standard Java SE 1.8 libraries. No external dependencies are required.

## Testing
The project includes JUnit 5 test cases to verify the correctness of the implementation. Run the provided test classes to ensure that your implementation meets the requirements.

## Credits

This project was developed as part of the **Algorithms and Data Structures Lab** course at the **University of Camerino**. The project template and instructions were provided by **Professor Luca Tesei**. Special thanks to Professor Tesei for his guidance and support throughout the course.

## Contact

For any questions or issues, please refer to the project documentation or contact me.