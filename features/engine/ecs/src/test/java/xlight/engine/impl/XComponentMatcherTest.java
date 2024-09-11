package xlight.engine.impl;

import com.badlogic.gdx.utils.Bits;
import com.google.common.truth.Truth;
import org.junit.Before;
import org.junit.Test;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.esc.component.BallComponent;
import xlight.engine.esc.component.EnemyComponent;
import xlight.engine.esc.component.InputComponent;
import xlight.engine.esc.component.PlayerComponent;
import xlight.engine.esc.component.WeaponComponent;

public class XComponentMatcherTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void should_match_matcher() {
        XEntityServiceImpl entityService = new XEntityServiceImpl();
        XComponentServiceImpl componentService = new XComponentServiceImpl(entityService);
        XEntity playerEntity = entityService.obtain();

        componentService.registerComponent(PlayerComponent.class);
        componentService.registerComponent(InputComponent.class);
        componentService.registerComponent(WeaponComponent.class);
        componentService.registerComponent(EnemyComponent.class);
        componentService.registerComponent(BallComponent.class);

        componentService.attachComponent(playerEntity, new PlayerComponent());
        componentService.attachComponent(playerEntity, new InputComponent());
        componentService.attachComponent(playerEntity, new WeaponComponent());

        Bits componentMask = playerEntity.getComponentMask();

        XComponentMatcherBuilder matcherBuilder = componentService.getMatcherBuilder();

        boolean contains = false;

        matcherBuilder.reset();
        matcherBuilder.all(PlayerComponent.class, InputComponent.class, WeaponComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();

        matcherBuilder.reset();
        matcherBuilder.all(WeaponComponent.class, InputComponent.class, PlayerComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();

        matcherBuilder.reset();
        matcherBuilder.all(PlayerComponent.class, InputComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();

        matcherBuilder.reset();
        matcherBuilder.all(InputComponent.class,  PlayerComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();

        matcherBuilder.reset();
        matcherBuilder.all(PlayerComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();

        matcherBuilder.reset();
        matcherBuilder.one(WeaponComponent.class, InputComponent.class, PlayerComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();

        matcherBuilder.reset();
        matcherBuilder.one(PlayerComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();

        // Entity have player component but not enemyComponent
        matcherBuilder.reset();
        matcherBuilder.one(EnemyComponent.class, PlayerComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();

        // Entity have player component but not enemyComponent
        matcherBuilder.reset();
        matcherBuilder.one(PlayerComponent.class, EnemyComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();

        // Entity don't have enemy component
        matcherBuilder.reset();
        matcherBuilder.one(EnemyComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isFalse();

        // Entity don't have enemy component
        matcherBuilder.reset();
        matcherBuilder.exclude(EnemyComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();

        // Entity have weapon component
        matcherBuilder.reset();
        matcherBuilder.exclude(WeaponComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isFalse();

        matcherBuilder.reset();
        matcherBuilder.all(PlayerComponent.class, InputComponent.class);
        matcherBuilder.exclude(WeaponComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isFalse();

        matcherBuilder.reset();
        matcherBuilder.all(PlayerComponent.class, InputComponent.class);
        matcherBuilder.exclude(BallComponent.class);
        contains = matcherBuilder.build().matches(componentMask);
        Truth.assertThat(contains).isTrue();
    }

    @Test
    public void test_matcher_hash() {
        XEntityServiceImpl entityService = new XEntityServiceImpl();
        XComponentServiceImpl componentService = new XComponentServiceImpl(entityService);

        componentService.registerComponent(PlayerComponent.class);
        componentService.registerComponent(InputComponent.class);
        componentService.registerComponent(WeaponComponent.class);
        componentService.registerComponent(EnemyComponent.class);
        componentService.registerComponent(BallComponent.class);

        XComponentMatcherBuilder matcherBuilder = componentService.getMatcherBuilder();

        matcherBuilder.reset();
        matcherBuilder.all(PlayerComponent.class, InputComponent.class, WeaponComponent.class);
        int hash1 = matcherBuilder.build().hashCode();

        matcherBuilder.reset();
        matcherBuilder.all(WeaponComponent.class, InputComponent.class, PlayerComponent.class);
        int hash2 = matcherBuilder.build().hashCode();
        Truth.assertThat(hash1).isEqualTo(hash2);

        matcherBuilder.reset();
        matcherBuilder.all(WeaponComponent.class, InputComponent.class, PlayerComponent.class);
        matcherBuilder.exclude(EnemyComponent.class);
        hash2 = matcherBuilder.build().hashCode();
        Truth.assertThat(hash1).isNotEqualTo(hash2);

        matcherBuilder.reset();
        matcherBuilder.all(WeaponComponent.class, InputComponent.class, PlayerComponent.class);
        matcherBuilder.one(EnemyComponent.class);
        hash2 = matcherBuilder.build().hashCode();
        Truth.assertThat(hash1).isNotEqualTo(hash2);

        matcherBuilder.reset();
        matcherBuilder.all(WeaponComponent.class, InputComponent.class);
        hash2 = matcherBuilder.build().hashCode();
        Truth.assertThat(hash1).isNotEqualTo(hash2);

        matcherBuilder.reset();
        matcherBuilder.all(WeaponComponent.class, InputComponent.class, EnemyComponent.class);
        hash2 = matcherBuilder.build().hashCode();
        Truth.assertThat(hash1).isNotEqualTo(hash2);
    }
}