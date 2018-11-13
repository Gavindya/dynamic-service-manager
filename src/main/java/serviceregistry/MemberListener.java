package serviceregistry;

public interface MemberListener {

    void onMemberAddedOrRemovedEvent(MembershipEvent event, boolean isSelfAdded);
    void onMemberUpdatedEvent(MembershipEvent event);
}
