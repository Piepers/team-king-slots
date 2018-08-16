package me.piepers.king.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class SlotTest {
    @Test
    public void test_that_if_no_reel_is_present_that_activate_payline_throws_exception() {
        Slot slot = new Slot(SlotId.of("1"), "Coolest Slot Ever", 0L, Instant.now(), "Bas", null, 0, 100);
        assertThatThrownBy(() -> slot.activatePaylineByReference(1)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void test_that_if_no_paylines_are_present_that_activate_payline_throws_exception() {
        Reel reel = Reel.of(1, 3);
        Slot slot = new Slot(SlotId.of("1"), "Coolest Slot Ever", 0L, Instant.now(), "Bas", reel, 0, 100);
        assertThatThrownBy(() -> slot.activatePaylineByReference(1)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void test_that_if_paylines_exist_but_payline_is_activated_that_doesnt_exist_throws_exception() {
        Slot slot = Slot.of(SlotType.CLASSIC, "Bas");
        assertThatThrownBy(() -> slot.activatePaylineByReference(100)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void test_that_if_paylines_exist_and_activated_by_existing_reference_that_it_is_activated() throws Exception {
        Slot slot = Slot.of(SlotType.CLASSIC, "Bas");
        Payline expected = slot.activatePaylineByReference(1);
        Payline p = slot.getReel().getPayLines().stream().filter(payline -> payline.getReference() == 1).findFirst().orElseThrow(Exception::new);
        assertThat(p.isActive()).isTrue();
        assertThat(expected).isEqualTo(p);
    }

    @Test
    public void test_that_if_no_paylines_are_present_that_deactivate_payline_throws_exception() {
        Reel reel = Reel.of(1, 3);
        Slot slot = new Slot(SlotId.of("1"), "Coolest Slot Ever", 0L, Instant.now(), "Bas", reel, 0, 100);
        assertThatThrownBy(() -> slot.deActivatePaylineByReference(1)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void test_that_if_paylines_exist_but_payline_is_deactivated_that_doesnt_exist_throws_exception() {
        Slot slot = Slot.of(SlotType.CLASSIC, "Bas");
        assertThatThrownBy(() -> slot.deActivatePaylineByReference(100)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void test_that_if_paylines_exists_and_deactivated_by_existing_reference_that_it_is_deactivated() throws Exception {
        Slot slot = Slot.of(SlotType.CLASSIC, "Bas");
        Payline expected = slot.deActivatePaylineByReference(1);
        Payline p = slot.getReel().getPayLines().stream().filter(payline -> payline.getReference() == 1).findFirst().orElseThrow(Exception::new);
        assertThat(p.isActive()).isFalse();
        assertThat(expected).isEqualTo(p);
    }
}
