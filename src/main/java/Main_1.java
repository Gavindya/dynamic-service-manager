public class Main_1 {

    public static void main(String[] args) {
        CuratorMembershipProtocol mp = new CuratorMembershipProtocol();
        mp.start();
        try {
            /**
             * Waits untill all the other members are discovered
             */
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mp.register();
    }
}
