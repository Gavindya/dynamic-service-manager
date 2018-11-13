//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.CuratorFrameworkFactory;
//import org.apache.curator.framework.recipes.cache.TreeCache;
//import org.apache.curator.retry.ExponentialBackoffRetry;
//
//public class NodeListener {
//
//    private CuratorFramework client;
//    private TreeCache cache;
//    private String connectionString;
//    private ServiceManager serviceManager;
//
//    public NodeListener(String connectionString) {
//        this.connectionString = connectionString;
//        this.createCuratorConnection();
//    }
//
//    private void createCuratorConnection() {
//        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
//                .connectString(connectionString)
//                .retryPolicy(new ExponentialBackoffRetry(1000/*retryInitialWaitMs*/, 3/*maxRetryCount*/));
//        this.client = builder.build();
//        this.client.start();
//        System.out.println("client for " + connectionString + " started");
//    }
//
//    public void subscribeToChangesOfBasePath(String path) {
//        try {
//            setTreeCache(path);
//            startListenerThread(path);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//    }
//
//    private void setTreeCache(String path) {
//        try {
//            cache = new TreeCache(client, path);
//            cache.start();
//            System.out.println("Tree cache started for Path = " + path);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void startListenerThread(String path) {
//        if (serviceManager == null) return;
//        ChildListenerThread childListenerThread = new ChildListenerThread(client, cache, path, serviceManager);
//        childListenerThread.start();
//        System.out.println("Listener thread started");
//    }
//
//    public void setServiceManager(ServiceManager serviceManager) {
//        this.serviceManager = serviceManager;
//    }
//
//}
