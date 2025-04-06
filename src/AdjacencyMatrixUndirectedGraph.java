import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Classe che implementa un grafo non orientato tramite matrice di adiacenza.
 * Non sono accettate etichette dei nodi null e non sono accettate etichette
 * duplicate nei nodi (che in quel caso sono lo stesso nodo).
 * 
 * I nodi sono indicizzati da 0 a nodeCoount() - 1 seguendo l'ordine del loro
 * inserimento (0 è l'indice del primo nodo inserito, 1 del secondo e così via)
 * e quindi in ogni istante la matrice di adiacenza ha dimensione nodeCount() *
 * nodeCount(). La matrice, sempre quadrata, deve quindi aumentare di dimensione
 * ad ogni inserimento di un nodo. Per questo non è rappresentata tramite array
 * ma tramite ArrayList.
 * 
 * Gli oggetti GraphNode<L>, cioè i nodi, sono memorizzati in una mappa che
 * associa ad ogni nodo l'indice assegnato in fase di inserimento. Il dominio
 * della mappa rappresenta quindi l'insieme dei nodi.
 * 
 * Gli archi sono memorizzati nella matrice di adiacenza. A differenza della
 * rappresentazione standard con matrice di adiacenza, la posizione i,j della
 * matrice non contiene un flag di presenza, ma è null se i nodi i e j non sono
 * collegati da un arco e contiene un oggetto della classe GraphEdge<L> se lo
 * sono. Tale oggetto rappresenta l'arco. Un oggetto uguale (secondo equals) e
 * con lo stesso peso (se gli archi sono pesati) deve essere presente nella
 * posizione j, i della matrice.
 * 
 * Questa classe non supporta i metodi di cancellazione di nodi e archi, ma
 * supporta tutti i metodi che usano indici, utilizzando l'indice assegnato a
 * ogni nodo in fase di inserimento.
 * 
 * @author Luca Tesei
 *
 */
public class AdjacencyMatrixUndirectedGraph<L> extends Graph<L> {
    /*
     * Le seguenti variabili istanza sono protected al solo scopo di agevolare
     * il JUnit testing
     */

    // Insieme dei nodi e associazione di ogni nodo con il proprio indice nella
    // matrice di adiacenza
    protected Map<GraphNode<L>, Integer> nodesIndex;

    // Matrice di adiacenza, gli elementi sono null o oggetti della classe
    // GraphEdge<L>. L'uso di ArrayList permette alla matrice di aumentare di
    // dimensione gradualmente ad ogni inserimento di un nuovo nodo.
    protected ArrayList<ArrayList<GraphEdge<L>>> matrix;

    /**
     * Crea un grafo vuoto.
     */
    public AdjacencyMatrixUndirectedGraph() {
        this.matrix = new ArrayList<ArrayList<GraphEdge<L>>>();
        this.nodesIndex = new HashMap<GraphNode<L>, Integer>();
    }

    @Override
    public int nodeCount() {
        // Prendo l'Insieme dei Nodi (ovvero le keys della Mappa nodesIndex) e le conto
        return nodesIndex.keySet().size();
    }

    @Override
    public int edgeCount() {
        // Se ho i nodi {s,u} entrambi hanno associato l'arco (s,u),
        // ma in edgeCount viene contato una volta e non due.
        return this.getEdges().size();
    }

    @Override
    public void clear() {
        // Uso il metodo clear() della classe Map e della classe ArrayList
        this.nodesIndex.clear();
        this.matrix.clear();
    }

    @Override
    public boolean isDirected() {
        // Questa classe implementa grafi non orientati
        return false;
    }

    @Override
    public Set<GraphNode<L>> getNodes() {
        // Prendo l'Insieme dei Nodi (ovvero le keys della Mappa nodesIndex) e lo restituisco
        return nodesIndex.keySet();
    }

    @Override
    public boolean addNode(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Tentativo di aggiungere un nodo null");
        // Aggiungo il nuovo nodo prima nella mappa, in questo modo definisco il suo indice
        // Uso putIsAbsent perchè se il nodo è già presente non ne viene sovrascritto l'indice
        if (nodesIndex.putIfAbsent(node, nodeCount()) == null) { // Se il nodo è stato aggiunto
            // Aggiungo una nuova colonna a tutte le righe esistenti e ci metto un obj null
            for (ArrayList<GraphEdge<L>> row : matrix) row.add(null);
            // Costruisco la nuova riga della matrice
            ArrayList<GraphEdge<L>> newRow = new ArrayList<>();
            // Riempio la nuova riga con tutti obj null
            for (int i = 0; i <= matrix.size(); i++) newRow.add(null);
            // Aggiungo la nuova riga della matrice
            matrix.add(newRow);
            // Inserimento riuscito
            return true;
        }
        // Nodo non aggiunto perchè era già presente
        return false;
    }

    @Override
    public boolean removeNode(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Tentativo di rimuovere un nodo null");
        // Prendo l'indice del nodo passato come parametro (se è presente nel grafo)
        Integer nodeIndex = nodesIndex.get(node);
        // Se il nodo non esiste non può essere rimosso, quindi ritorno false
        if (nodeIndex == null) return false; 
        // Se arrivo qui il nodo è presente nel grafo e può quindi essere rimosso

        // Fase 1: Rimuovere gli archi e la riga e la colonna dalla matrice

        // Rimuovo la riga corrispondente alla posizione del nodo nella matrice
        matrix.remove(nodeIndex.intValue());
        // Rimuovo la colonna corrispondente alla posizione del nodo nella matrice
        // Per farlo scorro tutte le righe della matrice e rimuovo la colonna interessata
        for (ArrayList<GraphEdge<L>> row : matrix) row.remove(nodeIndex.intValue());

        // Fase 2: Rimuovere il nodo dalla mappa <nodo : indice>

        nodesIndex.remove(node);
        // Devo decrementare di 1 tutti gli indici successivi a quello rimosso
        // Creo una nuova mappa temporanea, copia di nodesIndex, sulla quale itero e scorro le coppie di valori,
        // ma nel frattempo sto modificando la mappa originale, la quale non lancia eccezione perchè in realtà
        // sto iterando sulla sua copia
        for (Map.Entry<GraphNode<L>, Integer> couple_key_value : new HashMap<>(nodesIndex).entrySet()) {
            if (couple_key_value.getValue() > nodeIndex) nodesIndex.put(couple_key_value.getKey(), couple_key_value.getValue() - 1);
        }
        return true;
    }

    @Override
    public boolean containsNode(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Tentativo di cercare un nodo null");
        // Sfrutto il metodo containsKey delle Mappe (dato che i nodi sono le keys)
        return nodesIndex.containsKey(node);
    }

    @Override
    public GraphNode<L> getNodeOf(L label) {
        if (label == null) throw new NullPointerException("Il parametro passato non può essere null");
        // Scorro le keys della mappa
        for (GraphNode<L> node : nodesIndex.keySet()) {
            // Se trovo tra le keys il nodo che ha l'etichetta uguale a quella che sto cercando lo ritorno
            if (node.getLabel().equals(label)) return node;
        }
        // Se arrivo qui significa che non c'è nessun nodo che ha l'etichetta passata
        return null;
    }

    @Override
    public int getNodeIndexOf(L label) {
        if (label == null) throw new NullPointerException("Il parametro passato non può essere null");
        // Scorro le keys della mappa
        for (GraphNode<L> node : nodesIndex.keySet()) {
            // Se trovo tra le keys il nodo che ha l'etichetta uguale a quella che sto cercando
            // ritorno il value a lui associato
            if (node.getLabel().equals(label)) return nodesIndex.get(node);
        }
        // Lancio un'eccezione nel caso in cui la ricerca della Label non sia andata a buon fine
        throw new IllegalArgumentException("La Label interessata non coincide con nessuno dei Nodi nel Grafo");
    }

    @Override
    public GraphNode<L> getNodeAtIndex(int i) {
        // Se l'Indice è valido
        if (i >= 0 && i < nodeCount()) {
            // Scorro l'insieme delle coppie chiave-valore della mappa
            for (Map.Entry<GraphNode<L>, Integer> couple_key_value : nodesIndex.entrySet()) {
                // Appena raggiungo l'indice giusto restituisco il suo nodo
                if (couple_key_value.getValue() == i) return couple_key_value.getKey();
            }
        }
        // Se l'indice passato non era valido lancio un'eccezione
        throw new IndexOutOfBoundsException("L'Indice passato non è valido");
    }

    @Override
    public Set<GraphNode<L>> getAdjacentNodesOf(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Il parametro passato non può essere null");
        // Prendo l'indice del nodo, se il nodo non è presente allora nodeIndex è null e lancio un'eccezione
        Integer nodeIndex = nodesIndex.get(node);
        if (nodeIndex == null) throw new IllegalArgumentException("Il nodo passato non è nel Grafo");
        Set<GraphNode<L>> adjacentNodesOfNode = new HashSet<>();
        // Scorro la riga del nodo interessato e aggiungo i nodi adiacenti al set da restituire
        for (GraphEdge<L> arco : matrix.get(nodeIndex)) {
            if (arco != null) { // Se i due nodi sono collegati da un arco
                if (arco.getNode1().equals(node)) adjacentNodesOfNode.add(arco.getNode2()); // Aggiungo il nodo a sx
                else adjacentNodesOfNode.add(arco.getNode1()); // Aggiungo il nodo a dx
            }
        }
        return adjacentNodesOfNode;
    }

    @Override
    public Set<GraphNode<L>> getPredecessorNodesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }

    @Override
    public Set<GraphEdge<L>> getEdges() {
        Set<GraphEdge<L>> graph_edges = new HashSet<>();
        // Con questa implementazione controllo solamente dalla diagonale (compresa) in poi della matrice,
        // questo perchè essendo un grafo non orientato la matrice di adiacenza è simmetrica
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = i; j < matrix.size(); j++) {
                // Se non è nullo, aggiungo l'arco della posizione corrente al set da restituire
                if (matrix.get(i).get(j) != null) graph_edges.add(matrix.get(i).get(j));
            }
        }
        return graph_edges;
    }

    @Override
    public boolean addEdge(GraphEdge<L> edge) {
        if (edge == null) throw new NullPointerException("Impossibile inserire un arco null");
        // Se l'arco che si vuole aggiungere è Directed va lanciata eccezione
        if (edge.isDirected()) throw new IllegalArgumentException("Un arco Orientato non è aggiungibile");
        // Se non entrambi i nodi riferiti dall'arco appartengono alle keys di questa mappa 
        // si lancia un'eccezione in quanto l'arco non è inseribile
        if (!this.containsNode(edge.getNode1()) || !this.containsNode(edge.getNode2()))
            throw new IllegalArgumentException("almeno uno dei due nodi dell'arco non è presente");

        // Se si arriva qui entrambi i nodi interessati sono presenti sono presenti nel Grafo
        // Controllo se questo arco esiste già, in tal caso non posso inserirlo nuovamente
        // Nota bene: se edge passato come parametro è (u,v) e nella matrice ho già l'arco (v,u)
        // il metodo containsEdge ritorna comunque true
        if (this.containsEdge(edge)) return false;

        // Avendo escluso tutti i casi negativi procedo ad inserire l'arco
        // Prendo gli Indici dei Nodi interessati
        int indexNode1 = nodesIndex.get(edge.getNode1());
        int indexNode2 = nodesIndex.get(edge.getNode2());
        // Aggiungo l'arco in entrambe le posizioni (il grafo non orientato è simmetrico)
        matrix.get(indexNode1).set(indexNode2, edge);
        matrix.get(indexNode2).set(indexNode1, edge);
        return true;
    }

    @Override
    public boolean removeEdge(GraphEdge<L> edge) {
        if (edge == null) throw new NullPointerException("Tentativo di rimuovere un arco null");
        // Se non entrambi i nodi riferiti dall'arco appartengono alle keys di questa mappa 
        // si lancia un'eccezione in quanto l'arco non è inseribile
        if (!this.containsNode(edge.getNode1()) || !this.containsNode(edge.getNode2()))
            throw new IllegalArgumentException("almeno uno dei due nodi dell'arco non è presente");
        
        // Prendo gli Indici dei Nodi interessati
        int indexNode1 = nodesIndex.get(edge.getNode1());
        int indexNode2 = nodesIndex.get(edge.getNode2());
        // Se l'arco esiste lo rimuovo dalla matrice ed essendo un grafo non orientato 
        // rimuovo anche l'arco simmetrico
        if (matrix.get(indexNode1).get(indexNode2) != null) {
            matrix.get(indexNode1).set(indexNode2, null);
            matrix.get(indexNode2).set(indexNode1, null);
            return true;
        }
        // Se arrivo qui significa che l'arco da rimuovere non esisteva
        return false;
    }

    @Override
    public boolean containsEdge(GraphEdge<L> edge) {
        if (edge == null) throw new NullPointerException("Il parametro passato non può essere null");
        // Se non entrambi i nodi riferiti dall'arco appartengono alle keys di questa mappa 
        // si lancia un'eccezione in quanto l'arco non è inseribile
        if (!this.containsNode(edge.getNode1()) || !this.containsNode(edge.getNode2()))
            throw new IllegalArgumentException("almeno uno dei due nodi dell'arco non è presente");
        
        // Basta cercare nell'Indice corretto della Matrice se ci sta quel nodo
        // Prendo gli Indici dei Nodi interessati
        int indexNode1 = nodesIndex.get(edge.getNode1());
        int indexNode2 = nodesIndex.get(edge.getNode2());
        // Mi basta controllare una qualsiasi delle due posizioni della matrice dato che è simmetrica
        GraphEdge<L> edge_in_matrix = matrix.get(indexNode1).get(indexNode2);
        // Ritorno true se l'arco in quella posizione della matrice
        // è uguale a quello passato come parametro
        return edge.equals(edge_in_matrix);
    }

    @Override
    public Set<GraphEdge<L>> getEdgesOf(GraphNode<L> node) {
        if (node == null) throw new NullPointerException("Il parametro passato non può essere null");
        if (!this.containsNode(node)) throw new IllegalArgumentException("Nodo non presente nel Grafo");

        Set<GraphEdge<L>> node_edges = new HashSet<>();
        // Scorro ogni arco della riga interessata e aggiungo tutti i suoi elementi non nulli
        for (GraphEdge<L> arco : matrix.get(nodesIndex.get(node))) {
            if (arco != null) node_edges.add(arco);
        }
        return node_edges;
    }

    @Override
    public Set<GraphEdge<L>> getIngoingEdgesOf(GraphNode<L> node) {
        throw new UnsupportedOperationException(
                "Operazione non supportata in un grafo non orientato");
    }
}
