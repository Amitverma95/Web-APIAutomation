package pojo.booking;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Booking request/response body. Null fields are not serialised, so the same class is used
 * for full (POST/PUT) and partial (PATCH) payloads.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {

    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;
    private BookingDates bookingdates;
    private String additionalneeds;

    public Booking() {
    }

    public Booking(String firstname, String lastname, Integer totalprice, Boolean depositpaid,
            BookingDates bookingdates, String additionalneeds) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.bookingdates = bookingdates;
        this.additionalneeds = additionalneeds;
    }

    /** Copy of this booking with the non-null fields of the patch applied - the expected result of a PATCH. */
    public Booking merge(Booking patch) {
        return new Booking(
                patch.firstname != null ? patch.firstname : firstname,
                patch.lastname != null ? patch.lastname : lastname,
                patch.totalprice != null ? patch.totalprice : totalprice,
                patch.depositpaid != null ? patch.depositpaid : depositpaid,
                patch.bookingdates != null ? patch.bookingdates : bookingdates,
                patch.additionalneeds != null ? patch.additionalneeds : additionalneeds);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(Integer totalprice) {
        this.totalprice = totalprice;
    }

    public Boolean getDepositpaid() {
        return depositpaid;
    }

    public void setDepositpaid(Boolean depositpaid) {
        this.depositpaid = depositpaid;
    }

    public BookingDates getBookingdates() {
        return bookingdates;
    }

    public void setBookingdates(BookingDates bookingdates) {
        this.bookingdates = bookingdates;
    }

    public String getAdditionalneeds() {
        return additionalneeds;
    }

    public void setAdditionalneeds(String additionalneeds) {
        this.additionalneeds = additionalneeds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking other = (Booking) o;
        return Objects.equals(firstname, other.firstname) && Objects.equals(lastname, other.lastname)
                && Objects.equals(totalprice, other.totalprice) && Objects.equals(depositpaid, other.depositpaid)
                && Objects.equals(bookingdates, other.bookingdates) && Objects.equals(additionalneeds, other.additionalneeds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, totalprice, depositpaid, bookingdates, additionalneeds);
    }

    @Override
    public String toString() {
        return "Booking{firstname=" + firstname + ", lastname=" + lastname + ", totalprice=" + totalprice
                + ", depositpaid=" + depositpaid + ", bookingdates=" + bookingdates + ", additionalneeds=" + additionalneeds + "}";
    }
}
