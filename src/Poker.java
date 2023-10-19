import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Poker {
    static Scanner scanner = new Scanner(System.in);
    static class Player {
        ArrayList<Card> hand;
        int highcard;
        boolean fold = false;
        int chips = 1500;
        int antibet;
        int playbet; //TODO setup playbet!! if player doesnt fold they must put a playbet that is = antibet
        int pairPlusBet;
        int winmult = 0;

        public void newHand(Card one, Card two, Card three) {
            this.hand.subList(0, 3).clear();
            this.antibet = 0;
            this.pairPlusBet = 0;
            this.highcard = 0;
            this.playbet = 0;
            this.fold = false;
            this.winmult = 0;
            this.hand = new ArrayList<>();
            this.hand.add(one);
            this.hand.add(two);
            this.hand.add(three);

        }

        public Player(Card one, Card two, Card three) {
            this.hand = new ArrayList<>();
            this.hand.add(one);
            this.hand.add(two);
            this.hand.add(three);
        }

        public Card rawCard(int num) {
            return this.hand.get(num);
        }

        public String suitCheck(int num) {
            return this.hand.get(num).suit;
        }

        public String faceCheck(int num) {
            return this.hand.get(num).face;
        }

        public ArrayList<String> fullHandSuit() {
            ArrayList<String> cardsInfo = new ArrayList<>();
            cardsInfo.add((this.hand.get(0).suit));
            cardsInfo.add((this.hand.get(1).suit));
            cardsInfo.add((this.hand.get(2).suit));
            return cardsInfo;
        }

        public ArrayList<String> fullHandFace() {
            ArrayList<String> cardsInfo = new ArrayList<>();
            cardsInfo.add(this.hand.get(0).face);
            cardsInfo.add(this.hand.get(1).face);
            cardsInfo.add(this.hand.get(2).face);
            return cardsInfo;
        }


        public void highCardCal() {
            this.highcard = 0;
            for (Card card : this.hand) {
                if (highcard < card.value) {
                    this.highcard = card.value;
                }
            }
        }
        public int Calculate() {
            ArrayList<String> nums = new ArrayList<>();
            ArrayList<String> faces = new ArrayList<>();
            ArrayList<Integer> values = new ArrayList<>();
            boolean pair = false;
            boolean flush = false;
            boolean straight = false;
            boolean kind = false;
            boolean SF = false;
            String[] suits = new String[]{"Hearts", "Clubs", "Spades", "Diamonds"};
            String[] check = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
            for (int i = 0; i != 3; i++) {
                nums.add(faceCheck(i));
                faces.add(suitCheck(i));
                values.add(rawCard(i).value);
            }


            // Pair 1:1
            for (String num : check) {
                if (Collections.frequency(nums, num) == 2) {
                    pair = true;
                    break;
                }
            }
            // flush  3:1
            for (String suit : suits) {
                if (Collections.frequency(faces, suit) == 3) {
                    flush = true;
                    break;
                }
            }
            // Straight 6:1
            Collections.sort(values);
            if ((values.get(values.size() - 1) - values.get(0)) == 2) {
                straight = true;
            }
            //Straight Flush  40:1
            if (straight && flush) {
                SF = true;
            }
            // Three Of a kind 30:1
            for (String num : check) {
                if (Collections.frequency(nums, num) == 3) {
                    kind = true;
                    break;
                }
            }
            if (pair) this.winmult = 1;

            if (flush) this.winmult = 3;

            if (straight) this.winmult = 6;
            if (kind) this.winmult = 30;
            if (SF) this.winmult = 40;
            return this.winmult;
        }

    }
    static class Card {
        String suit;
        String face;
        int value;
        boolean ace = false;

        public Card(String suit, String face) {
            this.suit = suit;
            this.face = face;
        }

        public void checkValue() {
            if (isNumber(this.face)) {
                this.value = Integer.parseInt(this.face);
            } else {
                switch (this.face) {
                    case ("J"):
                        this.value = 11;
                        break;
                    case ("Q"):
                        this.value = 12;
                        break;
                    case ("K"):
                        this.value = 13;
                        break;
                    case ("A"):
                        this.value = 1;
                        this.ace = true;
                        break;
                }

            }

        }
    }


    public static void main(String[] args) {
        ArrayList<Card> cards = makeCards();
        Player player = new Player(draw(cards), draw(cards), draw(cards));
        Player dealer = new Player(draw(cards), draw(cards), draw(cards));
        clear(player);
        while(player.chips != 0) game(player,dealer,cards);
        System.out.println("You got kicked out.");
    }

    private static void game(Player player, Player dealer,ArrayList<Card> cards) {
        if (cards.size() <= 10) {
            cards = makeCards();
        }
        placingBet(player);
        divider("Your hand");
        revealHand(player);
        fold(player);
        if(!player.fold) {
            clear(player);
            divider("Your hand");
            revealHand(player);
            divider("Stakes");
            System.out.printf("Total Coins: %d anti-bet: %d pairplus bet: %d play bet: %d\n", player.chips, player.antibet, player.pairPlusBet, player.playbet);
            divider("Dealer's hand");
            revealHand(dealer);
            payday(player, dealer);
        }
        player.newHand(draw(cards), draw(cards), draw(cards));
        dealer.newHand(draw(cards), draw(cards), draw(cards));
        divider("Outcome");
        System.out.printf("Total Coins: %d anti-bet: %d pairplus bet: %d play bet: %d\n", player.chips, player.antibet, player.pairPlusBet, player.playbet);
        if(player.chips != 0) {
            System.out.println("Press enter to play again");
            scoreboard(player);
            scanner.nextLine();
            clear(player);
        }
    }

    private static void placingBet(Player player) {
        clear(player);
        System.out.print("\n What would you like to bet as your ante bet (Remember it you must have 2x this in chips for this.)\n");
        String user = scanner.nextLine();
        if(isNumber(user)){
            if((Integer.parseInt(user) * 2) > player.chips){
                System.out.println("Make sure you bet a number that you can double");
                placingBet(player);
            }else {
                player.chips -= Integer.parseInt(user);
                player.antibet = Integer.parseInt(user);
                pairPlusBetting(player);
            }
        }else {
            System.out.println("Make sure you put in a whole number");
            System.out.println("Press enter to try again");
            placingBet(player);
        }

    }

    private static void pairPlusBetting(Player player) {
        clear(player);
        System.out.printf("You have %d chips that you can bet \n What would you like to bet as your pair plus bet (Get rewards depending on what pair you get)\n",(player.chips - player.antibet));
        String user = scanner.nextLine();
        if(isNumber(user)){
            if(Integer.parseInt(user) > (player.chips - player.antibet)){
                System.out.println("Make sure have enough to bet");
                pairPlusBetting(player);
            }else{
                player.pairPlusBet = Integer.parseInt(user);
                player.chips -= player.pairPlusBet;
                clear(player);
            }
        }else {
            System.out.println("Make sure you put in a whole number");
            pairPlusBetting(player);
        }
    }


    private static void revealHand(Player player) {
        ArrayList<String> suit = player.fullHandSuit();
        ArrayList<String> face = player.fullHandFace();

        System.out.printf("The %s of %s, the %s of %s, and the %s of %s\n", face.get(0), suit.get(0), face.get(1), suit.get(1), face.get(2), suit.get(2));
    }

    private static void fold(Player player) {
        System.out.println("Would you like to keep playing and double down or fold? 1) Keep playing 2) Fold");
        try {
            int user = Integer.parseInt(scanner.nextLine());
            // int user = 1; Testing
            switch (user) {
                case (1):
                    player.chips -= player.antibet;
                    player.playbet = player.antibet;
                    break;
                case (2):
                    player.fold = true;
                    player.antibet = 0;
                    player.pairPlusBet = 0;
                    player.playbet = 0;
                    break;
                default:
                    throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Make sure that it is either 1 or 2.");
            fold(player);
        }
    }

    private static void payday(Player player, Player dealer) {
        highcard(player, dealer);
        pairbet(player);
    }

    private static void pairbet(Player player) {
        divider("Pair plus");
        int dealergives = (player.Calculate() * player.pairPlusBet);
        if (((dealergives + player.pairPlusBet) != player.pairPlusBet) && !player.fold) {
            player.chips += dealergives + player.pairPlusBet;
            switch (player.winmult){
                case(1):
                    System.out.printf("You got a pair dealer gave you %d and that puts you at a total of %d\n",dealergives,player.chips);
                    break;
                case(3):
                    System.out.printf("You got a flush dealer gave you %d and that puts you at a total of %d\n",dealergives,player.chips);
                    break;
                case(6):
                    System.out.printf("You got a straight dealer gave you %d and that puts you at a total of %d\n",dealergives,player.chips);
                    break;
                case(30):
                    System.out.printf("You got a three of a kind dealer gave you %d and that puts you at a total of %d\n",dealergives,player.chips);
                    break;
                case(40):
                    System.out.printf("You got a straight flush dealer gave you %d and that puts you at a total of %d\n",dealergives,player.chips);
                    break;


            }
        }else System.out.printf("You got nothing that puts you at a total of %d\n",player.chips);
        player.pairPlusBet = 0;
    }

    private static void highcard(Player one, Player two) {
        divider("High card");
        one.highCardCal();
        two.highCardCal();
        if (two.highcard <= 11 && !one.fold) {
            one.chips += one.antibet + one.antibet + one.playbet;
            System.out.printf("Dealer had less then a Jack. That puts you at %d \n",one.chips);
            one.antibet = 0;
            one.playbet = 0;
        }
        if ((two.highcard >= 12 && one.highcard > two.highcard) && !one.fold) {
            one.chips += one.antibet * 2 + one.playbet * 2;
            System.out.printf("Dealer had a %s and you had higher puts you at %d \n",two.highcard,one.chips);
            one.antibet = 0;
            one.playbet = 0;
        } else if (two.highcard >= 12 && (one.highcard <= two.highcard)) {
            System.out.printf("Dealer had a %s and you had lower puts you at %d \n",two.highcard,one.chips);
        }


    }

    private static Card draw(ArrayList<Card> cards) {
        Card temp = cards.get(0);
        cards.remove(0);
        return temp;
    }

    private static void shuffle(ArrayList<Card> cards) {
        Collections.shuffle(cards);
    }

    private static ArrayList<Card> makeCards() {
        ArrayList<Card> list = new ArrayList<>();
        String[] suits = new String[]{"Hearts", "Clubs", "Spades", "Diamonds"};
        String[] faces = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String suit : suits) {
            for (String face : faces) {
                Card card = new Card(suit, face);
                card.checkValue();
                list.add(card);

            }
        }
        shuffle(list);
        return list;
    }

    public static boolean isNumber(String num) {
        try {
            Integer.parseInt(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void clear(Player player) {
        System.out.print("\033[H\033[2J");
        scoreboard(player);
    }
    private static void scoreboard(Player player){
        System.out.print("\033[999C");
        String winPrint = "Chips " + player.chips;
        for (int i = 0; i < winPrint.length() - 1; i++) {
            System.out.print("\b");
        }
        System.out.println(winPrint);
    }
    public static void divider(String input) {
        System.out.printf("-----------------------------------------------%s-----------------------------------------------\n",input);
    }
}
