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

        public Player(Card one, Card two, Card three) {
            this.hand = new ArrayList<>();
            this.hand.add(one);
            this.hand.add(two);
            this.hand.add(three);
        }

        public ArrayList<String> cardCheck(int num) {
            ArrayList<String> singleCardInfo = new ArrayList<>();
            singleCardInfo.add(this.hand.get(num).suit);
            singleCardInfo.add(this.hand.get(num).face);
            singleCardInfo.add(Integer.toString(this.hand.get(num).value));
            return singleCardInfo;
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

        public void highCardCal() {
            this.highcard = 0;
            for (Card card : this.hand) {
                if (highcard < card.value) {
                    this.highcard = card.value;
                }
            }
        }
        public int Calculate(){
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
            for(String num : check) {
                if (Collections.frequency(nums, num) == 2) {
                    pair = true;
                    break;
                }
            }
            // flush  3:1
            for(String suit : suits) {
                if (Collections.frequency(faces, suit) == 3) {
                    flush = true;
                    break;
                }
            }
            // Straight 6:1
            Collections.sort(values);
            if((values.get(values.size()-1) - values.get(0)) == 2) {
                straight = true;
            }
            //Straight Flush  40:1
            if(straight && flush) {
                SF = true;
            }
            // Three Of a kind 30:1
            for(String num : check) {
                if (Collections.frequency(nums, num) == 3) {
                    kind = true;
                    break;
                }
            }
            int winmult = 0;
            if(pair) winmult = 1;

            if(flush)winmult = 3;

            if(straight)winmult = 6;
            if(kind)winmult = 30;
            if(SF)winmult = 40;
            return winmult;
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


    public static void main(String[] args) {                                //TODO SETUP UI
        ArrayList<Card> cards = makeCards();                                //TODO Make LOOPABLE
        if (cards.size() <= 10) {                                           //TODO BETTING
            cards = makeCards();
        }
        Player player = new Player(draw(cards), draw(cards), draw(cards));
        Player dealer = new Player(draw(cards), draw(cards), draw(cards));
        System.out.print(player.cardCheck(0));
        System.out.print(player.cardCheck(1));
        System.out.println(player.cardCheck(2));
        System.out.print(dealer.cardCheck(0));
        System.out.print(dealer.cardCheck(1));
        System.out.println(dealer.cardCheck(2));


//     Card t1 =new Card("Hearts", "2");
//        t1.checkValue();
//     Card t2 =new Card("Hearts", "3");
//        t2.checkValue();
//     Card t3 =new Card("Hearts", "4");
//        t3.checkValue();
//     Player testing = new Player(t1,t2,t3);
        fold(player);
        payday(player,dealer);
    }

    private static void fold(Player player) {
        System.out.println("Would you like to keep playing and double down or fold? 1) Keep playing 2) Fold");
        try {
            int user = Integer.parseInt(scanner.nextLine());
            switch (user){
                case(1):
                    player.chips -= player.antibet;
                    player.playbet = player.antibet;
                    break;
                case(2):
                    player.fold = true;
                    player.antibet = 0;
                    player.pairPlusBet = 0;
                    player.playbet = 0;
                    break;
                default:
                    throw new Exception();
            }
        } catch (Exception e){
            System.out.println("Make sure that it is either 1 or 2.");
            fold(player);
        }
    }

    private static void payday(Player player,Player dealer) {
        pairbet(player);
        highcard(player,dealer);
    }

    private static void pairbet(Player player){
        int dealergives = (player.Calculate() * player.pairPlusBet);
        if ((dealergives + player.pairPlusBet) != player.pairPlusBet) {
            player.chips += dealergives + player.pairPlusBet;
        }
        player.pairPlusBet = 0;
    }
    private static void highcard(Player one, Player two) {
        one.highCardCal();
        two.highCardCal();
        if (two.highcard <= 11 && !one.fold) {
            one.chips += one.antibet;
            one.antibet = 0;
        }
        if ((two.highcard >= 12 && one.highcard > two.highcard) && !one.fold) {
            one.chips += one.antibet + one.playbet;
            one.antibet = 0;
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
}
