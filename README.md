# WarZ Tactical Movement

一个为 Minecraft 1.20.1 Forge 开发的战术动作模组，提供真实的探头射击机制和第三人称动画效果。

## 功能特性

### 🎯 探头射击系统
- **左右探头**：使用 Q/E 键进行左右探头
- **两种模式**：支持按住模式和切换模式
- **碰撞检测**：探头时自动调整碰撞箱，防止穿墙
- **安全检查**：自动检测障碍物，防止卡墙

### 🎭 第三人称动画
- **头部倾斜**：探头时头部自然倾斜
- **身体倾斜**：配合头部动作的身体倾斜
- **手臂调整**：探头时手臂位置自然调整
- **平滑过渡**：所有动画都有平滑的过渡效果

### 🔫 TACZ 兼容性
- **完全兼容**：与 TACZ 武器模组完美集成
- **瞄准检测**：自动检测 TACZ 武器瞄准状态
- **智能禁用**：可配置瞄准时禁用探头
- **后坐力补偿**：探头时提供轻微的后坐力补偿

### ⚙️ 高度可配置
- **探头角度**：可调整最大探头角度（5-45度）
- **动画速度**：可调整动画过渡速度
- **碰撞设置**：可开关碰撞箱调整功能
- **性能优化**：可调整更新频率和插值设置

## 按键绑定

| 按键 | 功能 | 默认绑定 |
|------|------|----------|
| 左探头 | 向左探头 | Q |
| 右探头 | 向右探头 | E |
| 切换模式 | 切换按住/切换模式 | 未绑定 |
| 战术姿态 | 进入战术姿态 | Left Alt |
| 快速探头 | 快速探头 | T |

## 安装说明

### 前置要求
- Minecraft 1.20.1
- Minecraft Forge 47.2.0 或更高版本
- Java 17 或更高版本

### 安装步骤
1. 下载并安装 Minecraft Forge 1.20.1
2. 将 mod 文件放入 `.minecraft/mods` 文件夹
3. 启动游戏并享受战术动作！

### 可选依赖
- **TACZ**：获得完整的武器兼容性体验

## 配置选项

### 探头设置
- **探头角度**：调整最大探头角度
- **探头速度**：调整探头动画速度
- **启用碰撞**：开关探头时的碰撞箱调整
- **碰撞缩减**：调整碰撞箱缩减程度

### 第三人称动画
- **启用动画**：开关第三人称动画
- **头部倾斜角度**：调整头部倾斜程度
- **身体倾斜角度**：调整身体倾斜程度
- **动画速度**：调整动画过渡速度

### TACZ 兼容性
- **启用兼容**：开关 TACZ 兼容性
- **瞄准时禁用**：瞄准时是否禁用探头

### 性能设置
- **更新频率**：调整更新频率（影响性能）
- **平滑插值**：开关动画平滑插值

## 使用技巧

### 基础探头
1. 按住 Q 键向左探头
2. 按住 E 键向右探头
3. 松开按键返回正常姿态

### 切换模式
1. 绑定并按下"切换模式"键
2. 现在按一次 Q/E 开始探头，再按一次停止
3. 再次按下"切换模式"键返回按住模式

### 战术姿态
1. 按住 Left Alt 进入战术姿态
2. 在战术姿态下探头更加稳定
3. 松开按键退出战术姿态

### TACZ 集成
1. 装备 TACZ 武器
2. 探头功能会自动适配武器状态
3. 瞄准时探头行为会根据配置调整

## 开发信息

### 技术规格
- **Minecraft 版本**：1.20.1
- **Forge 版本**：47.2.0+
- **Gradle 版本**：7.6
- **Java 版本**：17

### 构建说明
```bash
# 克隆仓库
git clone https://github.com/warzteam/tactical-movement.git
cd tactical-movement

# 构建 mod
./gradlew build

# 运行开发环境
./gradlew runClient
```

### API 接口
本 mod 提供了完整的 API 接口，其他 mod 可以通过以下方式集成：

```java
// 获取玩家的战术能力
player.getCapability(TacticalCapabilityProvider.TACTICAL_CAPABILITY).ifPresent(cap -> {
    // 检查是否在探头
    boolean isPeeking = cap.isPeeking();
    
    // 获取探头方向
    ITacticalCapability.PeekDirection direction = cap.getPeekDirection();
    
    // 获取探头进度
    float progress = cap.getPeekProgress();
});
```

## 兼容性

### 已测试兼容
- ✅ TACZ (Timeless and Classics Guns)
- ✅ JEI (Just Enough Items)
- ✅ Optifine
- ✅ 大部分性能优化 mod

### 已知不兼容
- ❌ 某些修改玩家模型的 mod 可能有冲突
- ❌ 修改碰撞箱的 mod 可能有冲突

## 更新日志

### v1.0.0
- 初始版本发布
- 基础探头射击功能
- 第三人称动画系统
- TACZ 兼容性支持
- 完整的配置系统

## 支持与反馈

- **GitHub Issues**：[提交问题](https://github.com/warzteam/tactical-movement/issues)
- **Discord**：加入我们的 Discord 服务器
- **QQ群**：398893720

## 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 致谢

- Minecraft Forge 团队
- TACZ 开发团队
- 所有测试者和贡献者

---

**WarZ Development Team** © 2024
