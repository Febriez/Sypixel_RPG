package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.InteractiveGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 직업 선택 GUI
 * 플레이어가 직업을 선택할 수 있는 인터페이스
 *
 * @author Febrie, CoffeeTory
 */
public class JobSelectionGui implements InteractiveGui {

    private static final int GUI_SIZE = 54; // 6줄

    private final GuiManager guiManager;
    private final LangManager langManager;
    private final Player player;
    private final RPGPlayer rpgPlayer;
    private final Inventory inventory;
    private final Map<Integer, GuiItem> items = new HashMap<>();

    private JobType.JobCategory selectedCategory = JobType.JobCategory.WARRIOR;

    public JobSelectionGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                           @NotNull Player player, @NotNull RPGPlayer rpgPlayer) {
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.player = player;
        this.rpgPlayer = rpgPlayer;
        this.inventory = Bukkit.createInventory(this, GUI_SIZE,
                Component.text("직업 선택", ColorUtil.LEGENDARY));

        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("직업 선택", ColorUtil.LEGENDARY);
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player,
                            int slot, @NotNull ClickType click) {
        GuiItem item = items.get(slot);
        if (item != null && item.hasActions()) {
            item.executeAction(player, click);
        }
    }

    /**
     * GUI 레이아웃 설정
     */
    private void setupLayout() {
        setupBackground();
        setupCategoryTabs();
        setupJobDisplay();
        setupNavigationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 상단 테두리
        for (int i = 0; i < 9; i++) {
            if (i < 3 || i > 5) { // 카테고리 탭 위치 제외
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 중간 구분선
        for (int i = 18; i < 27; i++) {
            setItem(i, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        }

        // 하단 테두리
        for (int i = 45; i < 54; i++) {
            setItem(i, GuiFactory.createDecoration());
        }

        // 좌우 테두리
        setItem(9, GuiFactory.createDecoration());
        setItem(17, GuiFactory.createDecoration());
        setItem(36, GuiFactory.createDecoration());
        setItem(44, GuiFactory.createDecoration());
    }

    /**
     * 카테고리 탭 설정
     */
    private void setupCategoryTabs() {
        int tabSlot = 3;

        for (JobType.JobCategory category : JobType.JobCategory.values()) {
            boolean isSelected = category == selectedCategory;
            boolean isKorean = langManager.getPlayerLanguage(player).equals("ko_KR");

            GuiItem tabItem = GuiItem.clickable(
                    ItemBuilder.of(isSelected ? Material.ENCHANTED_BOOK : Material.BOOK)
                            .displayName(Component.text(category.getName(isKorean), category.getColor())
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.text(isSelected ? "▶ 선택됨" : "클릭하여 선택",
                                    isSelected ? ColorUtil.SUCCESS : ColorUtil.GRAY))
                            .flags(ItemFlag.values())
                            .glint(isSelected)
                            .build(),
                    clickPlayer -> {
                        selectedCategory = category;
                        refresh();
                        clickPlayer.playSound(clickPlayer.getLocation(),
                                org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    }
            );

            setItem(tabSlot++, tabItem);
        }
    }

    /**
     * 직업 표시
     */
    private void setupJobDisplay() {
        boolean isKorean = langManager.getPlayerLanguage(player).equals("ko_KR");

        // 카테고리 정보 표시
        GuiItem categoryInfo = GuiItem.display(
                ItemBuilder.of(selectedCategory.getIcon())
                        .displayName(Component.text(selectedCategory.getName(isKorean) + " 계열",
                                        selectedCategory.getColor())
                                .decoration(TextDecoration.BOLD, true))
                        .lore(getCategoryDescription(selectedCategory, isKorean))
                        .flags(ItemFlag.values())
                        .build()
        );
        setItem(13, categoryInfo);

        // 해당 카테고리의 직업들 표시
        List<JobType> categoryJobs = new ArrayList<>();
        for (JobType job : JobType.values()) {
            if (job.getCategory() == selectedCategory) {
                categoryJobs.add(job);
            }
        }

        // 직업 표시 위치: 29, 31, 33
        int[] jobSlots = {29, 31, 33};
        for (int i = 0; i < categoryJobs.size() && i < jobSlots.length; i++) {
            JobType job = categoryJobs.get(i);
            setItem(jobSlots[i], createJobItem(job, isKorean));
        }
    }

    /**
     * 직업 아이템 생성
     */
    private GuiItem createJobItem(@NotNull JobType job, boolean isKorean) {
        ItemBuilder builder = ItemBuilder.of(getJobMaterial(job))
                .displayName(Component.text(job.getIcon() + " " + job.getName(isKorean), job.getColor())
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(Component.text("최대 레벨: " + job.getMaxLevel(), ColorUtil.INFO))
                .addLore(Component.empty());

        // 직업 설명 추가
        List<Component> description = getJobDescription(job, isKorean);
        for (Component line : description) {
            builder.addLore(line);
        }

        builder.addLore(Component.empty())
                .addLore(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━", ColorUtil.DARK_GRAY))
                .addLore(Component.text("⚠ 주의: 직업은 한 번 선택하면 변경할 수 없습니다!", ColorUtil.ERROR))
                .addLore(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━", ColorUtil.DARK_GRAY))
                .addLore(Component.empty())
                .addLore(Component.text("▶ 클릭하여 이 직업 선택", ColorUtil.SUCCESS))
                .flags(ItemFlag.values())
                .glint(true);

        return GuiItem.clickable(
                builder.build(),
                clickPlayer -> {
                    // 확인 GUI 열기
                    openConfirmationGui(job);
                }
        );
    }

    /**
     * 직업별 아이템 재료
     */
    private Material getJobMaterial(@NotNull JobType job) {
        return switch (job) {
            case BERSERKER -> Material.DIAMOND_AXE;
            case BRUISER -> Material.IRON_SWORD;
            case TANK -> Material.SHIELD;
            case PRIEST -> Material.GOLDEN_APPLE;
            case DARK_MAGE -> Material.WITHER_SKELETON_SKULL;
            case MERCY -> Material.TOTEM_OF_UNDYING;
            case ARCHER -> Material.BOW;
            case SNIPER -> Material.CROSSBOW;
            case SHOTGUNNER -> Material.FIRE_CHARGE;
        };
    }

    /**
     * 카테고리 설명
     */
    private List<Component> getCategoryDescription(@NotNull JobType.JobCategory category, boolean isKorean) {
        List<Component> description = new ArrayList<>();

        switch (category) {
            case WARRIOR -> {
                description.add(Component.text(isKorean ? "근접 전투에 특화된 직업군" : "Specialized in melee combat",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "높은 체력과 방어력을 보유" : "High health and defense",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "전방에서 팀을 보호하는 역할" : "Protects the team from the front",
                        ColorUtil.GRAY));
            }
            case MAGE -> {
                description.add(Component.text(isKorean ? "마법 공격에 특화된 직업군" : "Specialized in magical attacks",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "강력한 광역 스킬 보유" : "Powerful area skills",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "지원과 공격을 겸비" : "Combines support and offense",
                        ColorUtil.GRAY));
            }
            case ARCHER -> {
                description.add(Component.text(isKorean ? "원거리 공격에 특화된 직업군" : "Specialized in ranged attacks",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "높은 기동성과 회피율" : "High mobility and dodge rate",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "정확한 타격과 치명타" : "Precise hits and critical strikes",
                        ColorUtil.GRAY));
            }
        }

        return description;
    }

    /**
     * 직업 설명
     */
    private List<Component> getJobDescription(@NotNull JobType job, boolean isKorean) {
        List<Component> description = new ArrayList<>();

        switch (job) {
            case BERSERKER -> {
                description.add(Component.text(isKorean ? "광폭한 전사" : "Berserk Warrior", ColorUtil.ERROR));
                description.add(Component.text(isKorean ? "체력이 낮을수록 강해지는 직업" : "Becomes stronger at low health",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "높은 공격력과 흡혈 능력" : "High damage and lifesteal",
                        ColorUtil.GRAY));
            }
            case BRUISER -> {
                description.add(Component.text(isKorean ? "균형잡힌 전사" : "Balanced Warrior", ColorUtil.ORANGE));
                description.add(Component.text(isKorean ? "공격과 방어의 균형" : "Balance of offense and defense",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "다양한 상황에 대처 가능" : "Adaptable to various situations",
                        ColorUtil.GRAY));
            }
            case TANK -> {
                description.add(Component.text(isKorean ? "철벽 수호자" : "Iron Wall Guardian", ColorUtil.NETHERITE));
                description.add(Component.text(isKorean ? "최고의 방어력과 체력" : "Highest defense and health",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "아군을 보호하는 스킬" : "Skills to protect allies",
                        ColorUtil.GRAY));
            }
            case PRIEST -> {
                description.add(Component.text(isKorean ? "신성한 사제" : "Holy Priest", ColorUtil.LEGENDARY));
                description.add(Component.text(isKorean ? "강력한 치유와 버프" : "Powerful healing and buffs",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "언데드에게 추가 피해" : "Extra damage to undead",
                        ColorUtil.GRAY));
            }
            case DARK_MAGE -> {
                description.add(Component.text(isKorean ? "어둠의 마법사" : "Dark Mage", ColorUtil.EPIC));
                description.add(Component.text(isKorean ? "강력한 저주와 디버프" : "Powerful curses and debuffs",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "적을 약화시키는 능력" : "Ability to weaken enemies",
                        ColorUtil.GRAY));
            }
            case MERCY -> {
                description.add(Component.text(isKorean ? "자비로운 치유사" : "Merciful Healer", ColorUtil.SUCCESS));
                description.add(Component.text(isKorean ? "즉시 치유와 부활" : "Instant healing and resurrection",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "팀의 생존력 극대화" : "Maximizes team survivability",
                        ColorUtil.GRAY));
            }
            case ARCHER -> {
                description.add(Component.text(isKorean ? "정통 궁수" : "Traditional Archer", ColorUtil.EMERALD));
                description.add(Component.text(isKorean ? "빠른 연사와 이동속도" : "Fast attack and movement speed",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "원거리 정밀 타격" : "Long-range precision strikes",
                        ColorUtil.GRAY));
            }
            case SNIPER -> {
                description.add(Component.text(isKorean ? "정밀 저격수" : "Precision Sniper", ColorUtil.INFO));
                description.add(Component.text(isKorean ? "극대화된 치명타 피해" : "Maximized critical damage",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "은신과 암살 능력" : "Stealth and assassination",
                        ColorUtil.GRAY));
            }
            case SHOTGUNNER -> {
                description.add(Component.text(isKorean ? "근거리 사수" : "Close-range Shooter", ColorUtil.WARNING));
                description.add(Component.text(isKorean ? "근거리 광역 피해" : "Close-range area damage",
                        ColorUtil.GRAY));
                description.add(Component.text(isKorean ? "폭발적인 화력" : "Explosive firepower",
                        ColorUtil.GRAY));
            }
        }

        return description;
    }

    /**
     * 네비게이션 버튼 설정
     */
    private void setupNavigationButtons() {
        // 뒤로가기
        if (guiManager != null) {
            setItem(48, GuiFactory.createBackButton(guiManager, langManager, player));
        }

        // 닫기
        setItem(50, GuiFactory.createCloseButton(langManager, player));
    }

    /**
     * 직업 선택 확인 GUI 열기
     */
    private void openConfirmationGui(@NotNull JobType job) {
        // TODO: 확인 GUI 구현
        // 임시로 바로 직업 설정
        if (rpgPlayer.setJob(job)) {
            player.sendMessage(Component.text("축하합니다! " + job.getKoreanName() + " 직업을 선택했습니다!",
                    ColorUtil.SUCCESS));
            player.closeInventory();

            // 프로필 GUI로 돌아가기
            if (guiManager != null) {
                guiManager.openProfileGui(player);
            }
        } else {
            player.sendMessage(Component.text("이미 직업이 있습니다!", ColorUtil.ERROR));
        }
    }

    /**
     * 아이템 설정
     */
    private void setItem(int slot, @NotNull GuiItem item) {
        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }
}