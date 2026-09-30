package pojo.booking;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDates {

    private String checkin;
    private String checkout;

    public BookingDates() {
    }

    public BookingDates(String checkin, String checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingDates)) return false;
        BookingDates other = (BookingDates) o;
        return Objects.equals(checkin, other.checkin) && Objects.equals(checkout, other.checkout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkin, checkout);
    }

    @Override
    public String toString() {
        return "{checkin=" + checkin + ", checkout=" + checkout + "}";
    }
}
