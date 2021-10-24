package ggc.partners;

import java.util.StringJoiner;

public class Partner implements Comparable<Partner> {

  // TODO: not complete
  private String id;
  private String name;
  private String address;

  public Partner(String id, String name, String address) {
    this.id = id;
    this.name = name;
    this.address = address;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public int compareTo(Partner partner) {
    // TODO
    return 0;
  }

  @Override
  public String toString() {
    return new StringJoiner("|")
            .add(this.getId())
            .add(this.getName())
            .add(this.getAddress())
            .add("NORMAL") // TODO
            .add("0") // TODO
            .add("0") // TODO
            .add("0") // TODO
            .add("0") // TODO
            .toString();
  }
}
