import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * 
 * Classe singoletto che implementa l'algoritmo di Kruskal per trovare un
 * Minimum Spanning Tree di un grafo non orientato, pesato e con pesi non
 * negativi. L'algoritmo implementato si avvale della classe
 * {@code ForestDisjointSets<GraphNode<L>>} per gestire una collezione di
 * insiemi disgiunti di nodi del grafo.
 * 
 * @author Luca Tesei (template), Luca Soricetti luca.soricetti@studenti.unicam.it (implementazione)
 * 
 * @param <L>
 *                tipo delle etichette dei nodi del grafo
 *
 */
public class KruskalMSP<L> {

    /*
     * Struttura dati per rappresentare gli insiemi disgiunti utilizzata
     * dall'algoritmo di Kruskal.
     */
    private ForestDisjointSets<GraphNode<L>> disjointSets;

    /**
     * Costruisce un calcolatore di un albero di copertura minimo che usa
     * l'algoritmo di Kruskal su un grafo non orientato e pesato.
     */
    public KruskalMSP() {
        this.disjointSets = new ForestDisjointSets<GraphNode<L>>();
    }

    /**
     * Utilizza l'algoritmo goloso di Kruskal per trovare un albero di copertura
     * minimo in un grafo non orientato e pesato, con pesi degli archi non
     * negativi. L'albero restituito non è radicato, quindi è rappresentato
     * semplicemente con un sottoinsieme degli archi del grafo.
     * 
     * @param g
     *              un grafo non orientato, pesato, con pesi non negativi
     * @return l'insieme degli archi del grafo g che costituiscono l'albero di
     *         copertura minimo trovato
     * @throw NullPointerException se il grafo g è null
     * @throw IllegalArgumentException se il grafo g è orientato, non pesato o
     *        con pesi negativi
     */
    public Set<GraphEdge<L>> computeMSP(Graph<L> g) {
        if (g == null) throw new NullPointerException("Il grafo passato è null");
        if (g.isDirected()) throw new IllegalArgumentException("Il grafo passato è orientato");

        // Controllo se tutti gli archi hanno pesi non negativi
        Set<GraphEdge<L>> graph_edges = g.getEdges();
        for (GraphEdge<L> arco : graph_edges) {
            if (!arco.hasWeight() || arco.getWeight() < 0) 
                throw new IllegalArgumentException("Il grafo contiene archi con peso non valido");
        }
        
        // Scorro tutti i nodi del Grafo e per ognuno creo un Insieme Singoletto
        Set<GraphNode<L>> graph_nodes = g.getNodes();
        for (GraphNode<L> nodo : graph_nodes) disjointSets.makeSet(nodo);

        // Trasformo l'Insieme degli archi in un ArrayList e, tramite HeapSort (Complessità O(n*logn)),
        // ordino gli archi in ordine di peso non decrescente
        ArrayList<GraphEdge<L>> edgeArrayList = new ArrayList<>(graph_edges);
        heapSort(edgeArrayList);

        Set<GraphEdge<L>> result = new HashSet<>();
        // Algoritmo di Kruskal
        // scorro tutti gli archi, prendendoli in ordine non decrescente
        for (GraphEdge<L> arco : edgeArrayList) {
            // controllo se tutti i nodi sono stati collegati dall'algoritmo, in tal caso
            // termino l'iterazione in quanto il minimo albero di copertura è stato creato
            // Nota Bene: la condizione prevede numero di archi == numero di nodi - 1 perchè 
            // il minimo albero di copertura contiene n-1 archi, dove n è il numero di nodi
            if (result.size() == graph_nodes.size()-1) break;
            // se ancora non sono stati collegati tutti i nodi allora controllo i due nodi di questo arco e:
            // 1. Se appartengono già allo stesso insieme non possiamo usare quest'arco perchè si creerebbe un ciclo;
            // 2. Se non appartengono allo stesso insieme unisco i loro due insiemi e aggiungo l'arco al risultato
            if (!disjointSets.findSet(arco.getNode1()).equals(disjointSets.findSet(arco.getNode2()))) {
                disjointSets.union(arco.getNode1(), arco.getNode2());
                result.add(arco);
            }
        }
        // Uso clear() dato che è necessario ad un oggetto istanza di questa classe 
        // per essere eseguito con un grafo differente passato come parametro
        disjointSets.clear();
        return result;
    }

    // Implementazione dell'Algoritmo heapSort per un ArrayList
    private void heapSort(ArrayList<GraphEdge<L>> edges) {
        // Prendo la dimensione dell'ArrayList di Archi
        int heap_size = edges.size();
        // Costruisco il Max-Heap
        buildMaxHeap(edges, heap_size);
        // Eseguo il MaxHeap
        for (int i = heap_size-1; i > 0; i--) {
            // Sposto la radice (il massimo) alla fine della lista
            GraphEdge<L> temp = edges.get(0);
            edges.set(0, edges.get(i));
            edges.set(i, temp);
            // Chiamo heapify sulla nuova radice per ripristinare il MaxHeap
            maxHeapify(edges, i, 0);
        }
    }

    // Implementazione del Metodo BuildMaxHeap, necessario per l'algoritmo HeapSort
    private void buildMaxHeap(ArrayList<GraphEdge<L>> edges, int heap_size) {
        for (int i = heap_size / 2 - 1; i >= 0; i--) {
            maxHeapify(edges, heap_size, i);
        }
    }

    // Implementazione del Metodo maxHeapify, necessario per l'algoritmo HeapSort
    private void maxHeapify(ArrayList<GraphEdge<L>> edges, int heap_size, int i) {
        int max = i; // Inizializzo il nodo più grande come il nodo corrente
        int left = 2 * i + 1; // Figlio sinistro del nodo corrente
        int right = 2 * i + 2; // Figlio destro del nodo corrente

        // Se left.weight > nodoAttuale.weight allora left diventa il nuovo max
        if (left < heap_size && edges.get(left).getWeight() > edges.get(max).getWeight()) max = left;
        // Se right.weight > max.weight allora right diventa il nuovo max
        if (right < heap_size && edges.get(right).getWeight() > edges.get(max).getWeight()) max = right;
        // Se il nodo più grande non è la radice scambio di posizione il nodo attuale ed il massimo
        if (max != i) {
            GraphEdge<L> temp = edges.get(i);
            edges.set(i, edges.get(max));
            edges.set(max, temp);
            // Chiamata ricorsiva per proseguire l'esecuzione di maxHeapify
            maxHeapify(edges, heap_size, max);
        }
    }
}