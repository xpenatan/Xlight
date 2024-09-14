package xlight.engine.impl;

import com.google.common.truth.Truth;
import org.junit.Before;
import org.junit.Test;
import xlight.engine.ecs.entity.XEntityState;

public class XEntityArrayImplTest {

    XEntityArray entities;

    @Before
    public void setUp() {
        entities = new XEntityArray(2);
    }

    @Test
    public void validateSize() {
        Truth.assertThat(entities.items.length).isEqualTo(2);
        Truth.assertThat(entities.items[0]).isNotNull();
        Truth.assertThat(entities.items[1]).isNotNull();
        Truth.assertThat(entities.items[0].state).isEqualTo(XEntityState.RELEASE);
        Truth.assertThat(entities.items[1].state).isEqualTo(XEntityState.RELEASE);
        Truth.assertThat(entities.items[1]).isNotNull();
    }

    @Test
    public void validateObtainAndRelease() {
        XEntityImpl e1 = entities.obtainEntity();
        Truth.assertThat(entities.items[0]).isEqualTo(e1);
        Truth.assertThat(e1.state).isEqualTo(XEntityState.DETACHED);
        XEntityImpl e2 = entities.obtainEntity();
        Truth.assertThat(entities.items[1]).isEqualTo(e2);
        Truth.assertThat(e2.state).isEqualTo(XEntityState.DETACHED);
        Truth.assertThat(entities.items.length).isEqualTo(2);

        XEntityImpl e3 = entities.obtainEntity();
        Truth.assertThat(entities.items[2]).isEqualTo(e3);
        Truth.assertThat(e3.state).isEqualTo(XEntityState.DETACHED);

        XEntityImpl e4 = entities.obtainEntity();
        Truth.assertThat(entities.items[3]).isEqualTo(e4);
        Truth.assertThat(e4.state).isEqualTo(XEntityState.DETACHED);

        boolean e1Flag1 = entities.releaseEntity(e1.getId());
        boolean e1Flag2 = entities.releaseEntity(e1.getId());
        boolean e2Flag1 = entities.releaseEntity(e2.getId());
        Truth.assertThat(e1Flag1).isTrue();
        Truth.assertThat(e1Flag2).isFalse();
        Truth.assertThat(e2Flag1).isTrue();
        Truth.assertThat(e1.state).isEqualTo(XEntityState.RELEASE);
        Truth.assertThat(e2.state).isEqualTo(XEntityState.RELEASE);

        XEntityImpl reusableE1 = entities.obtainEntity();
        Truth.assertThat(entities.items[reusableE1.getId()]).isEqualTo(reusableE1);
        Truth.assertThat(reusableE1.state).isEqualTo(XEntityState.DETACHED);
    }


    @Test
    public void validateAttachAndDetach() {
        XEntityImpl e1 = entities.obtainEntity();
        XEntityImpl e2 = entities.obtainEntity();
        XEntityImpl e3 = entities.obtainEntity();
        Truth.assertThat(e1.state).isEqualTo(XEntityState.DETACHED);
        Truth.assertThat(e2.state).isEqualTo(XEntityState.DETACHED);
        Truth.assertThat(e3.state).isEqualTo(XEntityState.DETACHED);

        boolean f1 = entities.attachEntity(e2.getId());
        boolean f11 = entities.attachEntity(e2.getId());
        boolean f2 = entities.attachEntity(e3.getId());

        Truth.assertThat(f1).isTrue();
        Truth.assertThat(f11).isFalse();
        Truth.assertThat(f2).isTrue();
        Truth.assertThat(e1.state).isEqualTo(XEntityState.DETACHED);
        Truth.assertThat(e2.state).isEqualTo(XEntityState.ATTACHED);
        Truth.assertThat(e3.state).isEqualTo(XEntityState.ATTACHED);
    }
}