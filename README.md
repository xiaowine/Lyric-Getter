![Release Download](https://img.shields.io/github/downloads/xiaowine/Lyric-Getter/total?style=flat-square)  
[![Release Version](https://img.shields.io/github/v/release/xiaowine/Lyric-Getter?style=flat-square)](https://github.com/xiaowine/Lyric-Getter/releases/latest)  
[![GitHub license](https://img.shields.io/github/license/xiaowine/Lyric-Getter?style=flat-square)](https://github.com/xiaowine/Lyric-Getter/LICENSE)  
[![GitHub Star](https://img.shields.io/github/stars/xiaowine/Lyric-Getter?style=flat-square)](https://github.com/xiaowine/Lyric-Getter/stargazers)  
[![GitHub Fork](https://img.shields.io/github/forks/xiaowine/Lyric-Getter?style=flat-square)](https://github.com/xiaowine/Lyric-Getter/network/members)  
![GitHub Repo size](https://img.shields.io/github/repo-size/xiaowine/Lyric-Getter?style=flat-square&color=3cb371)  
[![GitHub Repo Languages](https://img.shields.io/github/languages/top/xiaowine/Lyric-Getter?style=flat-square)](https://github.com/xiaowine/Lyric-Getter/search?l=koltin)  
[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2F577fkj%2FStatusBarLyric%2Fbadge%3Fref%3Dmain&style=flat)](https://actions-badge.atrox.dev/xiaowine/Lyric-Getter/goto?ref=main)  
![GitHub Star](https://img.shields.io/github/stars/xiaowine/Lyric-Getter.svg?style=social)

# 这是什么东西？

#### 这是一个Xposed模块（现支持LSPosed\LSPatch），通过Hook获取音乐软件的歌词，提供给其他模块\软件使用

# 为什么我的歌词不隐藏？

### 因为模块通过监听媒体通知事件，来判断是否应该隐藏歌词。而部分音乐软件默认通知样式为自定义的，所以需要将通知样式改为系统样式

> 支持软件

- 网易云音乐（需8.0.40以上开启状态栏歌词，其余开启车载歌词）
- 酷狗音乐（需开启车载歌词）
- 酷狗音乐概念版（需开启车载歌词）
- 酷我音乐（需开启车载歌词）
- QQ音乐（需开启状态栏歌词）
- 洛雪音乐（需开启桌面歌词）
- 小米音乐（需开启车载歌词）
- 魅族音乐（需开启车载歌词）
- 咪咕音乐（需开启状态栏歌词，新版由于咪咕移除，已无法使用）
- RPlayer（需开启车载歌词）
- APlayer（需开启状态栏歌词）
- 汽水音乐（需佩戴耳机）（需开启车载歌词）  
  `使用API的音乐软件不在README中，并且可能不在模块作用域中，具体需要开启哪些功能请自行测试`  
  截至 1.0.0.2 版本

---

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=xiaowine/Lyric-Getter&type=Timeline)](https://star-history.com/#xiaowine/Lyric-Getter&Timeline)
