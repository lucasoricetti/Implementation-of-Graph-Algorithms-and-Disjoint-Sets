import java.util.HashSet;
import java.util.Set;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Classe singoletto che realizza un calcolatore delle componenti connesse di un
 * grafo non orientato utilizzando una struttura dati efficiente (fornita dalla
 * classe {@ForestDisjointSets<GraphNode<L>>}) per gestire insiemi disgiunti di
 * nodi del grafo che sono, alla fine del calcolo, le componenti connesse.
 * 
 * @author Luca Tesei (template), Luca Soricetti luca.soricetti@studenti.unicam.it (implementazione)
 *
 * @param <L>
 *                il tipo delle etichette dei nodi del grafo
 */
public class UndirectedGraphConnectedComponentsComputer<L> {

    /*
     * Struttura dati per gli insiemi disgiunti.
     */
    private ForestDisjointSets<GraphNode<L>> f;

    /**
     * Crea un calcolatore di componenti connesse.
     */
    public UndirectedGraphConnectedComponentsComputer() {
        this.f = new ForestDisjointSets<GraphNode<L>>();
    }

    /**
     * Calcola le componenti connesse di un grafo non orientato utilizzando una
     * collezione di insiemi disgiunti.
     * 
     * @param g
     *              un grafo non orientato
     * @return un insieme di componenti connesse, ognuna rappresentata da un
     *         insieme di nodi del grafo
     * @throws NullPointerException
     *                                      se il grafo passato è nullo
     * @throws IllegalArgumentException
     *                                      se il grafo passato è orientato
     */
    public Set<Set<GraphNode<L>>> computeConnectedComponents(Graph<L> g) {
        if (g == null) throw new NullPointerException("Il grafo passato è null");
        if (g.isDirected()) throw new IllegalArgumentException("Il grafo passato è orientato");

        // Controllo se g è vuoto
        if (g.isEmpty()) return new HashSet<Set<GraphNode<L>>>();

        // Scorro tutti i nodi del Grafo g e per ognuno creo un Insieme Singoletto
        for (GraphNode<L> nodo : g.getNodes()) f.makeSet(nodo);
        // Scorro tutti gli archi del Grafo g e per ognuno controllo se i suoi due nodi appartengono allo
        // stesso insieme. Se ciò non è vero, unisco i loro due insiemi
        for (GraphEdge<L> arco : g.getEdges()) {
            GraphNode<L> node1 = arco.getNode1();
            GraphNode<L> node2 = arco.getNode2();
            if (!(f.findSet(node1).equals(f.findSet(node2)))) f.union(node1, node2);
        }
        
        // Creo il Set di Set da Ritornare
        Set<Set<GraphNode<L>>> connectedComponents = new HashSet<Set<GraphNode<L>>>();

        // Per risparmiare operazioni prima prendo tutti i rappresentanti
        // Poi controllo tutti i loro Insiemi e li aggiungo
        for (GraphNode<L> representative : f.getCurrentRepresentatives()) {
            connectedComponents.add(f.getCurrentElementsOfSetContaining(representative));
        }
        // Uso clear() dato che è necessario per permettere ad un oggetto istanza di questa classe 
        // per essere eseguito con un grafo differente passato come parametro
        f.clear();
        return connectedComponents;
    }
}
