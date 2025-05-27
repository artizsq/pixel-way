//package com.pixelway.gameScreens.minigames; // Убедитесь, что это правильный пакет
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.physics.box2d.Contact;
//import com.badlogic.gdx.physics.box2d.ContactImpulse;
//import com.badlogic.gdx.physics.box2d.ContactListener;
//import com.badlogic.gdx.physics.box2d.Manifold;
//import com.pixelway.MainClass;
//import com.pixelway.models.characters.Boss;
//import com.pixelway.models.projectiles.PlayerBullet; // Ваша пуля игрока
//import com.pixelway.models.characters.MiniPlayer;
//import com.pixelway.models.projectiles.BossAttack; // Общий класс для атак босса
//
//
//public class AttackContactListener implements ContactListener {
//
//    private MainClass game; // Ссылка на основной класс игры (может быть полезна для перехода между экранами или глобальных состояний)
//    private MiniPlayer player; // Ссылка на объект игрока
//    private Boss boss;         // Ссылка на объект босса
//
//    public AttackContactListener(MainClass game, MiniPlayer player, Boss boss) {
//        this.game = game;
//        this.player = player;
//        this.boss = boss;
//    }
//
//    @Override
//    public void beginContact(Contact contact) {
//        // Получаем пользовательские данные (UserData) из фикстур, которые столкнулись.
//        // Мы устанавливаем UserData на Box2D Body (или Fixture) в ваших классах моделей (MiniPlayer, Boss, PlayerBullet, BossAttack).
//        Object fixtureAData = contact.getFixtureA().getUserData();
//        Object fixtureBData = contact.getFixtureB().getUserData();
//
//        // Проверяем, что UserData не null. Это важно, так как не у всех Box2D тел может быть UserData.
//        if (fixtureAData == null || fixtureBData == null) {
//            return;
//        }
//
//        // --- Обработка столкновения пули игрока с боссом ---
//        // Проверяем все возможные комбинации (A - пуля, B - босс, или наоборот)
//        if (fixtureAData instanceof PlayerBullet && fixtureBData instanceof Boss) {
//            handlePlayerBulletBossCollision((PlayerBullet) fixtureAData, (Boss) fixtureBData);
//        } else if (fixtureBData instanceof PlayerBullet && fixtureAData instanceof Boss) {
//            handlePlayerBulletBossCollision((PlayerBullet) fixtureBData, (Boss) fixtureAData);
//        }
//
//        // --- Обработка столкновения атаки босса с игроком ---
//        // Проверяем все возможные комбинации (A - атака босса, B - игрок, или наоборот)
//        if (fixtureAData instanceof BossAttack && fixtureBData instanceof MiniPlayer) {
//            handleBossAttackPlayerCollision((BossAttack) fixtureAData, (MiniPlayer) fixtureBData);
//        } else if (fixtureBData instanceof BossAttack && fixtureAData instanceof MiniPlayer) {
//            handleBossAttackPlayerCollision((BossAttack) fixtureBData, (MiniPlayer) fixtureAData);
//        }
//
//        // Можно добавить другие типы столкновений здесь, если они появятся (например, игрок со стеной)
//    }
//
//    /**
//     * Вспомогательный метод для обработки столкновения пули игрока с боссом.
//     */
//    private void handlePlayerBulletBossCollision(PlayerBullet bullet, Boss targetBoss) {
//        // Наносим урон боссу
//        targetBoss.takeDamage(bullet.getDamage());
//        // Помечаем пулю для удаления (фактическое удаление тела происходит в BossBattleScreen)
//        bullet.markForRemoval();
//        Gdx.app.log("Contact", "Пуля игрока попала в босса! Урон: " + bullet.getDamage() + ". Здоровье босса: " + targetBoss.getHealth());
//    }
//
//    /**
//     * Вспомогательный метод для обработки столкновения атаки босса с игроком.
//     */
//    private void handleBossAttackPlayerCollision(BossAttack attack, MiniPlayer targetPlayer) {
//        // Наносим урон игроку
//        targetPlayer.takeDamage();
//        // Помечаем атаку для удаления (фактическое удаление тела происходит в BossBattleScreen)
//        attack.markForRemoval();
//        Gdx.app.log("Contact", "Атака босса попала в игрока! Урон: " + attack.getDamage() + ". Здоровье игрока: " + targetPlayer.getHealth());
//    }
//
//
//    @Override
//    public void endContact(Contact contact) {
//        // Вызывается, когда два фикстуры перестают соприкасаться.
//        // В этой игре, вероятно, не так критично, но может быть полезно для статусов (например, "на земле").
//    }
//
//    @Override
//    public void preSolve(Contact contact, Manifold oldManifold) {
//        // Вызывается после обнаружения столкновения, но до того, как оно будет решено.
//        // Можно использовать для изменения свойств столкновения (например, отключить его, изменить трение).
//    }
//
//    @Override
//    public void postSolve(Contact contact, ContactImpulse impulse) {
//        // Вызывается после того, как столкновение было решено.
//        // Можно получить информацию о силах, приложенных во время столкновения.
//    }
//}
