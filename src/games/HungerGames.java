package games;

import java.util.ArrayList;

/**
 * This class contains methods to represent the Hunger Games using BSTs.
 * Moves people from input files to districts, eliminates people from the game,
 * and determines a possible winner.
 * 
 * @author Pranay Roni
 * @author Maksims Kurjanovics Kravcenko
 * @author Kal Pandit
 */
public class HungerGames {

    private ArrayList<District> districts;  // all districts in Panem.
    private TreeNode            game;       // root of the BST. The BST contains districts that are still in the game.

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Default constructor, initializes a list of districts.
     */
    public HungerGames() {
        districts = new ArrayList<>();
        game = null;
        StdRandom.setSeed(2023);
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Sets up Panem, the universe in which the Hunger Games takes place.
     * Reads districts and people from the input file.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPanem(String filename) { 
        StdIn.setFile(filename);  // open the file - happens only once here
        setupDistricts(filename); 
        setupPeople(filename);
    }

    /**
     * Reads the following from input file:
     * - Number of districts
     * - District ID's (insert in order of insertion)
     * Insert districts into the districts ArrayList in order of appearance.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupDistricts (String filename) {

        // WRITE YOUR CODE HERE
        int numDistricts = StdIn.readInt();
        for(int i = 0; i < numDistricts; i++){
            int index = StdIn.readInt();
            District current = new District(index);
            districts.add(current);
        }
    }

    /**
     * Reads the following from input file (continues to read from the SAME input file as setupDistricts()):
     * Number of people
     * Space-separated: first name, last name, birth month (1-12), age, district id, effectiveness
     * Districts will be initialized to the instance variable districts
     * 
     * Persons will be added to corresponding district in districts defined by districtID
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPeople (String filename) {

        // WRITE YOUR CODE HERE
        int numPeople = StdIn.readInt();
        for(int i = 0; i < numPeople; i++){
            String firstname = StdIn.readString();
            String lastname = StdIn.readString();
            int month = Integer.parseInt(StdIn.readString());
            int age = Integer.parseInt(StdIn.readString());
            int id = Integer.parseInt(StdIn.readString());
            int effectiveness = Integer.parseInt(StdIn.readString());
            boolean tessera;
            if (age >= 12 && age < 18){
                tessera = true;
            }
            else{
                tessera = false;
            }        
            Person current = new Person(month, firstname, lastname, age, id, effectiveness);
            current.setTessera(tessera);

            int size = districts.size();
            for(int j = 0; j < size; j++){
                District check = districts.get(j);
                if (check.getDistrictID() == id){
                    if (month % 2 == 0){
                        check.addEvenPerson(current);
                    }
                    else{
                        check.addOddPerson(current);
                    }
                }
            }
        }
    }

    /**
     * Adds a district to the game BST.
     * If the district is already added, do nothing
     * 
     * @param root        the TreeNode root which we access all the added districts
     * @param newDistrict the district we wish to add
     */
    public void addDistrictToGame(TreeNode root, District newDistrict) {

        // WRITE YOUR CODE HERE
        if (root==null){
            TreeNode temp = new TreeNode(newDistrict, null, null);
            districts.remove(newDistrict);
            game = temp;
            return;
        }
        TreeNode current = root;
        TreeNode prev = null;
        while(current!=null){
            if (current.getDistrict().getDistrictID() == newDistrict.getDistrictID()){
                game = root;
                return;
            }
            else if(current.getDistrict().getDistrictID() > newDistrict.getDistrictID()){
                prev = current;
                current = current.getLeft();
                if (current==null){
                    TreeNode temp = new TreeNode(newDistrict, null, null);
                    prev.setLeft(temp);
                    districts.remove(newDistrict);
                }
            }
            else if(current.getDistrict().getDistrictID() < newDistrict.getDistrictID()){
                prev = current;
                current = current.getRight();
                if (current==null){
                    TreeNode temp = new TreeNode(newDistrict, null, null);
                    prev.setRight(temp);
                    districts.remove(newDistrict);
                }
            }
        }
        game = root;
    }

    /**
     * Searches for a district inside of the BST given the district id.
     * 
     * @param id the district to search
     * @return the district if found, null if not found
     */
    public District findDistrict(int id) {

        // WRITE YOUR CODE HERE
        if (game == null){
            return null;
        }
        District temp = findDistrictHelper(id, game);

        return temp; // update this line
    }

    //Private method used to make recursive solution for the findDistrict() method
    private District findDistrictHelper(int id, TreeNode help){
        if (help == null){
            return null;
        }
        else if (help.getDistrict().getDistrictID() == id){
            return help.getDistrict();
        }
        else if (help.getDistrict().getDistrictID() < id){
            return findDistrictHelper(id, help.getRight());
        }
        else {
            return findDistrictHelper(id, help.getLeft());
        }
    }

    /**
     * Selects two duelers from the tree, following these rules:
     * - One odd person and one even person should be in the pair.
     * - Dueler with Tessera (age 12-18, use tessera instance variable) must be
     * retrieved first.
     * - Find the first odd person and even person (separately) with Tessera if they
     * exist.
     * - If you can't find a person, use StdRandom.uniform(x) where x is the respective 
     * population size to obtain a dueler.
     * - Add odd person dueler to person1 of new DuelerPair and even person dueler to
     * person2.
     * - People from the same district cannot fight against each other.
     * 
     * @return the pair of dueler retrieved from this method.
     */
    public DuelPair selectDuelers() {
        
        // WRITE YOUR CODE HERE
        if (game == null){
            return null;
        }
        TreeNode checkTreeOne = game;
        Person personOne = null;
        Person personTwo = null;
        personOne = personSelector(1, "odd", checkTreeOne, -1);
        if (personOne == null){
            personTwo = personSelector(1, "even", checkTreeOne, -1);
        }
        else {
            personTwo = personSelector(1, "even", checkTreeOne, personOne.getDistrictID());
        }
        if (personOne == null && personTwo == null){
            personOne = personSelector(2, "odd", checkTreeOne, -1);
            personTwo = personSelector(2, "even", checkTreeOne, personOne.getDistrictID());
        }
        else if (personTwo != null && personOne == null){
            personOne = personSelector(2, "odd", checkTreeOne, personTwo.getDistrictID());
        }
        else if (personTwo == null && personOne != null){
            personTwo = personSelector(2, "even", checkTreeOne, personOne.getDistrictID());
        }
        DuelPair pair = new DuelPair(personOne, personTwo);
        return pair; // update this line
    }


    //method for selectDuelers
    private Person personSelector(int type, String personType, TreeNode current, int otherId){
        if (type == 1){
            District presentDistrict = current.getDistrict();
            if (personType == "odd"){
                ArrayList<Person> population = presentDistrict.getOddPopulation();
                int length = population.size();
                for(int i=0; i < length; i++){
                    Person personBeingChecked = population.get(i);
                    if (personBeingChecked.getTessera() == true){
                        population.remove(personBeingChecked);
                        presentDistrict.setOddPopulation(population);
                        for(int j=0;j < districts.size();j++){
                            if (districts.get(j).getDistrictID() == presentDistrict.getDistrictID()){
                                districts.set(j, presentDistrict);
                            }
                        }
                        return personBeingChecked;
                    }
                }
                if (current.getLeft() != null){
                    Person left = personSelector(type, personType, current.getLeft(), otherId);
                    if (left != null){
                        return left;
                    }
                }
                if (current.getRight() != null){
                    Person right = personSelector(type, personType, current.getRight(), otherId);
                    if (right != null){
                        return right;
                    }
                }
                return null;
            }
            else if (personType == "even"){
                ArrayList<Person> population = presentDistrict.getEvenPopulation();
                int length = population.size();
                for(int i=0; i < length; i++){
                    Person personBeingChecked = population.get(i);
                    if (personBeingChecked.getTessera() == true){
                        population.remove(personBeingChecked);
                        presentDistrict.setEvenPopulation(population);
                        for(int j=0;j < districts.size();j++){
                            if (districts.get(j).getDistrictID() == presentDistrict.getDistrictID()){
                                districts.set(j, presentDistrict);
                            }
                        }
                        return personBeingChecked;
                    }
                }
                if (current.getLeft() != null){
                    Person left = personSelector(type, personType, current.getLeft(), otherId);
                    if (left != null){
                        return left;
                    }
                }
                if (current.getRight() != null){
                    Person right = personSelector(type, personType, current.getRight(), otherId);
                    if (right != null){
                        return right;
                    }
                }
                return null;
            }
        }
        else {
            District presentDistrict = current.getDistrict();
            if (personType == "odd"){
                ArrayList<Person> population = presentDistrict.getOddPopulation();
                int length = population.size();
                if( otherId != presentDistrict.getDistrictID()){
                    int random = StdRandom.uniform(length);
                    Person personBeingChecked = population.get(random);
                    population.remove(personBeingChecked);
                    presentDistrict.setOddPopulation(population);
                    for(int j=0;j < districts.size();j++){
                        if (districts.get(j).getDistrictID() == presentDistrict.getDistrictID()){
                            districts.set(j, presentDistrict);
                        }
                    }
                    return personBeingChecked;
                }
                if (current.getLeft() != null){
                    Person left = personSelector(type, personType, current.getLeft(), otherId);
                    if (left != null){
                        return left;
                    }
                }
                if (current.getRight() != null){
                    Person right = personSelector(type, personType, current.getRight(), otherId);
                    if (right != null){
                        return right;
                    }
                }
                return null;
            }
            else if (personType == "even"){
                ArrayList<Person> population = presentDistrict.getEvenPopulation();
                int length = population.size();
                if( otherId != presentDistrict.getDistrictID()){
                    int random = StdRandom.uniform(length);
                    Person personBeingChecked = population.get(random);
                    population.remove(personBeingChecked);
                    presentDistrict.setOddPopulation(population);
                    for(int j=0;j < districts.size();j++){
                        if (districts.get(j).getDistrictID() == presentDistrict.getDistrictID()){
                            districts.set(j, presentDistrict);
                        }
                    }
                    return personBeingChecked;
                }
                if (current.getLeft() != null){
                    Person left = personSelector(type, personType, current.getLeft(), otherId);
                    if (left != null){
                        return left;
                    }
                }
                if (current.getRight() != null){
                    Person right = personSelector(type, personType, current.getRight(), otherId);
                    if (right != null){
                        return right;
                    }
                }
                return null;
            }
        }
        return null;
    }


    /**
     * Deletes a district from the BST when they are eliminated from the game.
     * Districts are identified by id's.
     * If district does not exist, do nothing.
     * 
     * This is similar to the BST delete we have seen in class.
     * 
     * @param id the ID of the district to eliminate
     */
    public void eliminateDistrict(int id) {
        if (game == null){
            return;
        }
        TreeNode ptr = game;
        TreeNode prev = null;
        boolean found = false;
        while(ptr != null){
            if (ptr.getDistrict().getDistrictID() == id){
                found = true;
                break;
            }
            else if (ptr.getDistrict().getDistrictID() > id){
                prev = ptr;
                ptr = ptr.getLeft();
            }
            else {
                prev = ptr;
                ptr = ptr.getRight();
            }
        }

        if (found == false){
            return;
        }
        //zero child
        if (ptr.getLeft() == null && ptr.getRight() == null){
            if (prev == null){
                game = null;
            }
            else if (prev.getDistrict().getDistrictID() > ptr.getDistrict().getDistrictID()){
                prev.setLeft(null);
            }
            else if (prev.getDistrict().getDistrictID() < ptr.getDistrict().getDistrictID()){
                prev.setRight(null);
            }
        }
        //one child
        else if ((ptr.getLeft() != null && ptr.getRight() == null) || (ptr.getLeft() == null && ptr.getRight() != null)){
            if (prev == null){
                if (ptr.getLeft() == null){
                    game = ptr.getRight();
                }
                else{
                    game = ptr.getLeft();
                }
            }
            else if (prev.getDistrict().getDistrictID() > ptr.getDistrict().getDistrictID()){
                if (ptr.getLeft() == null){
                    prev.setLeft(ptr.getRight());
                }
                else{
                    prev.setLeft(ptr.getLeft());
                }
            }
            else if (prev.getDistrict().getDistrictID() < ptr.getDistrict().getDistrictID()){
                if (ptr.getLeft() == null){
                    prev.setRight(ptr.getRight());
                }
                else{
                    prev.setRight(ptr.getLeft());
                }
            }
        } 
        //two child
        else{
            TreeNode finder = ptr;
            finder = ptr.getRight();
            TreeNode finderPrev = ptr;
            while (finder.getLeft() != null){
                finderPrev = finder;
                finder = finder.getLeft();
            }
            if(finderPrev.getDistrict().getDistrictID() == id){
                finderPrev.setRight(null);
            }
            else{
                finderPrev.setLeft(null);
            }

            ptr.setDistrict(finder.getDistrict());
        }
        // WRITE YOUR CODE HERE
    }

    /**
     * Eliminates a dueler from a pair of duelers.
     * - Both duelers in the DuelPair argument given will duel
     * - Winner gets returned to their District
     * - Eliminate a District if it only contains a odd person population or even
     * person population
     * 
     * @param pair of persons to fight each other.
     */
    public void eliminateDueler(DuelPair pair) {
        Person winner = null;
        Person loser = null;
        if (pair.getPerson1() == null){
            winner = pair.getPerson2();
        }
        else if(pair.getPerson2() == null){
            winner = pair.getPerson1();
        }
        else{
            Person personOne = pair.getPerson1(); 
            Person personTwo = pair.getPerson2(); 
            winner = personOne.duel(personTwo);
            if (winner.getFirstName() == personOne.getFirstName() && winner.getLastName() == personOne.getLastName()){
                loser = personTwo;
            }
            else{
                loser = personOne;
            }
        }

        //winner handling
        District winnerHome = findDistrict(winner.getDistrictID());
        if (winner.getBirthMonth()%2==0){
            ArrayList<Person> population = winnerHome.getEvenPopulation();
            population.add(winner);
            winnerHome.setEvenPopulation(population);
        }
        else{
            ArrayList<Person> population = winnerHome.getOddPopulation();
            population.add(winner);
            winnerHome.setOddPopulation(population);
        }

        //loser handling
        if (loser != null){
            District loserHome = findDistrict(loser.getDistrictID());
            ArrayList<Person> oddpopulation = loserHome.getOddPopulation();
            ArrayList<Person> evenpopulation = loserHome.getEvenPopulation();
            if (oddpopulation.size()==0 || evenpopulation.size() == 0){
                eliminateDistrict(loser.getDistrictID());
            }
        }
        // WRITE YOUR CODE HERE
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Obtains the list of districts for the Driver.
     * 
     * @return the ArrayList of districts for selection
     */
    public ArrayList<District> getDistricts() {
        return this.districts;
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Returns the root of the BST
     */
    public TreeNode getRoot() {
        return game;
    }
}
