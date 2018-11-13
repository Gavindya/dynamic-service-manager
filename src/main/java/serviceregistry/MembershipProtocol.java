package serviceregistry;

//import com.grydtech.msstack.core.annotation.AutoInjected;
//import com.grydtech.msstack.core.annotation.FrameworkComponent;
//import com.grydtech.msstack.core.annotation.Value;
//import com.grydtech.msstack.core.configuration.ApplicationConfiguration;

import configuration.ApplicationConfiguration;

//@FrameworkComponent
public abstract class MembershipProtocol {

//    @Value
    protected static ApplicationConfiguration applicationConfiguration;

    private static MembershipProtocol instance;

    public static MembershipProtocol getInstance() {
        return instance;
    }

    public abstract Member register();

    //ToDo: need to change implementation (member details already in application config)
    public abstract Member updateMember(Member member);

    //ToDo: need to change implementation (member details already in application config)
    public abstract void removeMember(String memberName);

    public abstract void listen(String name, MemberListener memberListener);

    //start the membership protocol -create client etc
    public abstract void start();

    public abstract void stop();

}
