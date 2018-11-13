package serviceregistry;

import java.util.ArrayList;

public class Member {

    private String id;
    private String name;
    private String host;
    private int port;
    private int numberOfPartitions;
    private ArrayList<Integer> partitions;
    private int startDataOffset;
    private int endDataOffset;

    public Member() {
        this.partitions = new ArrayList<>();
    }

    public int getStartDataOffset() {
        return startDataOffset;
    }

    public Member setStartDataOffset(int startDataOffset) {
        this.startDataOffset = startDataOffset;
        return this;
    }

    public int getEndDataOffset() {
        return endDataOffset;
    }

    public Member setEndDataOffset(int endDataOffset) {
        this.endDataOffset = endDataOffset;
        return this;
    }

    public int getNumberOfPartitions() {
        return numberOfPartitions;
    }

    public Member setNumberOfPartitions(int numberOfPartitions) {
        this.numberOfPartitions = numberOfPartitions;
        return this;
    }

    public ArrayList<Integer> getPartitions() {
        return partitions;
    }

    public Member setPartitions(Integer partition) {
        this.partitions.add(partition);
        return this;
    }

    public Member setPartitions(ArrayList<Integer> partitions) {
        this.partitions = partitions;
        return this;
    }

    public String getId() {
        return id;
    }

    public Member setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Member setName(String name) {
        this.name = name;
        return this;
    }

    public String getHost() {
        return host;
    }

    public Member setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Member setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
