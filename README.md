# Only Crates

从 Actually Additions 模组中提取的独立板条箱模组，并添加了更多功能。
适用于 Minecraft 1.12.2 Forge。

## 板条箱

提供三种容量的板条箱，每页 117 格（9×13），支持翻页浏览，兼容漏斗和管道。：

- **小型** — 1 页
- **中型** — 2 页
- **大型** — 3 页

## 升级物品

- **箱子转板条箱升级** — 将原版箱子转为小型板条箱
- **小型转中型 / 中型转大型升级** — 提升板条箱容量
- **潜影盒升级** — 破坏时保留箱内物品
- **防爆升级** — 防止被爆炸摧毁

升级方式：潜行右键已放置的板条箱。

## 自定义板条箱

通过配置文件 `config/onlycrates/onlycrates.cfg` 可添加自定义存储箱，格式为 `方块ID/显示名称/页数`：

```
extra_crates {
    S:extra_crates <
        "mega_crate/超大型板条箱/5"
    >
}
```
将 16×16 的 PNG 贴图放入 `config/onlycrates/textures/` 文件夹，文件名与 block_id 一致即可使用自定义贴图。

## 许可证

MIT License