import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//ATTENZIONE: è vietato includere import a pacchetti che non siano della Java SE

/**
 * Implementazione dell'interfaccia <code>DisjointSets<E></code> tramite una
 * foresta di alberi ognuno dei quali rappresenta un insieme disgiunto. Si
 * vedano le istruzioni o il libro di testo Cormen et al. (terza edizione)
 * Capitolo 21 Sezione 3.
 * 
 * @author Luca Tesei (template), Luca Soricetti luca.soricetti@studenti.unicam.it (implementazione)
 *
 * @param <E>
 *                il tipo degli elementi degli insiemi disgiunti
 */
public class ForestDisjointSets<E> implements DisjointSets<E> {

    /*
     * Mappa che associa ad ogni elemento inserito il corrispondente nodo di un
     * albero della foresta. La variabile è protected unicamente per permettere
     * i test JUnit.
     */
    protected Map<E, Node<E>> currentElements;
    
    /*
     * Classe interna statica che rappresenta i nodi degli alberi della foresta.
     * Gli specificatori sono tutti protected unicamente per permettere i test
     * JUnit.
     */
    protected static class Node<E> {
        /*
         * L'elemento associato a questo nodo
         */
        protected E item;

        /*
         * Il parent di questo nodo nell'albero corrispondente. Nel caso in cui
         * il nodo sia la radice allora questo puntatore punta al nodo stesso.
         */
        protected Node<E> parent;

        /*
         * Il rango del nodo definito come limite superiore all'altezza del
         * (sotto)albero di cui questo nodo è radice.
         */
        protected int rank;

        /**
         * Costruisce un nodo radice con parent che punta a se stesso e rango
         * zero.
         * 
         * @param item
         *                 l'elemento conservato in questo nodo
         * 
         */
        public Node(E item) {
            this.item = item;
            this.parent = this;
            this.rank = 0;
        }

    }

    /**
     * Costruisce una foresta vuota di insiemi disgiunti rappresentati da
     * alberi.
     */
    public ForestDisjointSets() {
        this.currentElements = new HashMap<E, Node<E>>();
    }

    @Override
    public boolean isPresent(E e) {
        // Lancia eccezione se e è null
        if (e == null) throw new NullPointerException("La label passata non può essere null");
        // Sfrutto il metodo containsKey della Map
        return currentElements.containsKey(e);
    }

    /*
     * Crea un albero della foresta consistente di un solo nodo di rango zero il
     * cui parent è se stesso.
     */
    @Override
    public void makeSet(E e) {
        // Nota Bene: il metodo isPresent lancia eccezione se e è null
        if (isPresent(e)) throw new IllegalArgumentException("Il parametro è già presente");
        // Il costruttore di Node<E> in automatico setta, alla creazione del nuovo nodo, 
        // node.parent = node e rank = 0
        Node<E> node = new Node<>(e);
        currentElements.put(e, node);
    }

    /*
     * L'implementazione del find-set deve realizzare l'euristica
     * "compressione del cammino". Si vedano le istruzioni o il libro di testo
     * Cormen et al. (terza edizione) Capitolo 21 Sezione 3.
     */
    @Override
    public E findSet(E e) {
        // Nota Bene: il metodo isPresent lancia eccezione se e è null
        if (!isPresent(e)) throw new IllegalArgumentException("Il parametro non è nella Foresta");

        // Implementazione del metodo secondo lo pseudocodice nel Capitolo 21, Sezione 3 del libro di testo
        Node<E> node = currentElements.get(e);
        if (!node.equals(node.parent)) {
            node.parent = currentElements.get(findSet(node.parent.item)); // Path compression
        }
        return node.parent.item;
    }

    /*
     * L'implementazione dell'unione deve realizzare l'euristica
     * "unione per rango". Si vedano le istruzioni o il libro di testo Cormen et
     * al. (terza edizione) Capitolo 21 Sezione 3. In particolare, il
     * rappresentante dell'unione dovrà essere il rappresentante dell'insieme il
     * cui corrispondente albero ha radice con rango più alto. Nel caso in cui
     * il rango della radice dell'albero di cui fa parte e1 sia uguale al rango
     * della radice dell'albero di cui fa parte e2 il rappresentante dell'unione
     * sarà il rappresentante dell'insieme di cui fa parte e2.
     */
    @Override
    public void union(E e1, E e2) {
        // Nota Bene: il metodo isPresent lancia eccezione se e è null
        if (!isPresent(e1) || !isPresent(e2)) throw new IllegalArgumentException("Almeno uno dei parametri non è nella Foresta");
        // Implementazione del metodo secondo lo pseudocodice nel Capitolo 21, Sezione 3 del libro di testo
        Node<E> r1 = currentElements.get(findSet(e1));
        Node<E> r2 = currentElements.get(findSet(e2));
        // Se e1 ed e2 appartengono già allo stesso Insieme non faccio nulla
        if (r1.equals(r2)) return; 
        // Se arrivo qui posso unire i due Insiemi
        // Se r1 ha rank maggiore diventa lui il rappresentante del nuovo Insieme
        if (r1.rank > r2.rank) r2.parent = r1;
        else { // Se i due rank sono uguali, il rappresentante diventa il secondo, ovvero r2
            r1.parent = r2;
            if (r1.rank == r2.rank) r2.rank++;
        }
    }

    @Override
    public Set<E> getCurrentRepresentatives() {
        Set<E> representatives = new HashSet<>();
        // Scorro tutti gli item nella mappa e cerco il loro rappresentante di insieme
        for (E item : currentElements.keySet()) {
            // Se un rappresentante era già stato aggiunto non viene aggiunto nuovamente
            // per le caratteristiche del metodo add dei Set
            representatives.add(findSet(item));
        }
        return representatives;
    }

    @Override
    public Set<E> getCurrentElementsOfSetContaining(E e) {
        // Nota Bene: il metodo isPresent lancia eccezione se e è null
        if (!isPresent(e)) throw new IllegalArgumentException("Il parametro non è nella Foresta");
        Set<E> elementsInSet = new HashSet<>();
        // Trovo il rappresentante dell'item passato come parametro
        E representative = findSet(e);
        // Scorro tutti gli item nella foresta e aggiungo tutti quelli che hanno lo stesso rappresentante
        // (ovvero tutti quelli che appartengono allo stesso insieme)
        for (E element : currentElements.keySet()) {
            if (findSet(element).equals(representative)) elementsInSet.add(element);
        }
        return elementsInSet;
    }

    @Override
    public void clear() {
        // Sfrutto il metodo clear delle Mappe
        currentElements.clear();
    }
}