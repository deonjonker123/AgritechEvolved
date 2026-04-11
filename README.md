
# AgriTech: Evolved

AgriTech: Evolved is a comprehensive agricultural automation mod for Minecraft that enhances farming with advanced machines, modules, and extensive mod compatibility.

## Core Features

### **Automated Planters**

-   **Basic Planter**: A simple wooden planter that automatically grows crops and saplings. Automatically outputs drops to a container under it. Available in all 11 vanilla wood types.
- **Advanced Planter**: A high-tech planter with energy consumption, module slots, fertilizer support, and enhanced automation capabilities

### **Cloche**
A glass bell jar that attaches to any planter, boosting both growth speed and harvest yield. Stacks with fertilizer and modules for maximum efficiency. Configurable speed and yield multipliers.

- Right-click a planter with a cloche to attach it
- Shift-right-click with an empty hand to detach and recover it
- Breaking a cloched planter drops both items separately

### **Machines**
- **Composter**: Converts organic materials into biomass fuel with configurable input ratios
- **Biomass Burner**: Generates RF energy from crude biomass, biomass, and compacted biomass
- **Energy Capacitors**: Three tiers of energy storage (Tier 1: 500k RF, Tier 2: 1M RF, Tier 3: 4M RF)

### **Enhancement Modules**
- **Speed Modules (MK1-MK3)**: Increase processing speed at the cost of higher power consumption
- **Yield Modules (MK1-MK3)**: Boost harvest yields while reducing processing speed

### **Specialized Blocks**
- **Mulch**: Natural growth accelerator providing 50% speed boost
- **Infused Farmland**: Premium soil offering 100% growth speed increase

### **Fertilizer System**

Fertilizer support with configurable speed and yield multipliers:

-   Vanilla bone meal
-   Mystical Agriculture fertilizers
-   Immersive Engineering fertilizers
-   Forbidden & Arcanus arcane bone meal

Fertilizer can be automated via hopper or pipe into any of the four cardinal sides of the planter.

## Mod Compatibility

### **Supported Crop Mods**

- Mystical Agriculture & Mystical Agradditions
- Farmer's Delight
- Ars Nouveau
- Silent Gear
- Immersive Engineering
- Occultism
- Cobblemon
- Pam's HarvestCraft 2 - Crops
- Actually Additions
- Croptopia

### **Supported Tree Mods**

- Ars Nouveau & Ars Elemental
- Forbidden & Arcanus
- Integrated Dynamics
- Silent Gear
- Occultism
- Cobblemon
- Pam's HarvestCraft 2 - Trees
- Croptopia
- EvilCraft

### **Supported Soil Mods**

-   Mystical Agriculture farmlands
-   Farmer's Delight soils
-   Just Dire Things goosoils

## Advanced Configuration System

1.  **Mod Compatibility Toggles**: Enable/disable specific mod integrations per-mod, so you only load what you need.
2.  **Crop/Sapling/Soil Database**: Comprehensive JSON-based system defining what grows on what, with full TOML override support for custom rules.
3.  **Balance Configuration**: Fine-tune module effectiveness, power consumption, and processing times

### **Live Config Reloading**

No server/client restart required. Changes to crops, soils, fertilizers via the overrider toml, or compatibility settings can be applied instantly via in-game commands.

| Command                                | Effect                                      |
|----------------------------------------|---------------------------------------------|
| `/agritechevolved reload`              | Reloads all configs                         |
| `/agritechevolved reload plantables`   | Reloads the crop/soil/sapling database only |
| `/agritechevolved reload compostables` | Reloads the compostable database only       |
| `/agritechevolved reload config`       | Reloads the main TOML config only           |

Failed reloads report errors directly in chat rather than silently failing.

### **Interactive Placement**

-   Right-click with seeds to insert directly into planters
-   Right-click with saplings for instant placement
-   Right-click with soil blocks for instant placement
-   Right-click with hoes to till compatible blocks
- Right-click with mystical essence to convert vanilla farmland to the mystical farmland

### **Visual Feedback**

-   Real-time rendering of planted crops and soil types
-   Progress bars for all processing operations
-   Audio feedback for successful interactions

## Energy System
Uses RF power with configurable consumption rates:
-    Advanced Planter: 64 RF/t base consumption
-    Composter: 64 RF/t base consumption
-    Module power scaling based on effectiveness

## JEI Integration

-   Compatible crop/soil combinations of the planters

## Jade Integration

-   Displays current crop or sapling name
-   Shows growth stage and progress percentage
-   Shows active soil type and its growth modifier
-   Shows active fertilizer when one is slotted
- Shows cloche status with speed and yield modifiers