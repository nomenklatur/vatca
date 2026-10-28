---
name: Serene Editorial Reader
colors:
  surface: '#faf9ff'
  surface-dim: '#d8d9e4'
  surface-bright: '#faf9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f3fe'
  surface-container: '#ecedf8'
  surface-container-high: '#e6e7f3'
  surface-container-highest: '#e0e2ed'
  on-surface: '#181b23'
  on-surface-variant: '#45464e'
  inverse-surface: '#2d3039'
  inverse-on-surface: '#eff0fb'
  outline: '#75767f'
  outline-variant: '#c5c6cf'
  surface-tint: '#505d85'
  primary: '#06163a'
  on-primary: '#ffffff'
  primary-container: '#1d2b50'
  on-primary-container: '#8593be'
  inverse-primary: '#b8c5f3'
  secondary: '#37675b'
  on-secondary: '#ffffff'
  secondary-container: '#baeddd'
  on-secondary-container: '#3d6d61'
  tertiary: '#211600'
  on-tertiary: '#ffffff'
  tertiary-container: '#3b2a00'
  on-tertiary-container: '#ba8d13'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae1ff'
  primary-fixed-dim: '#b8c5f3'
  on-primary-fixed: '#0a193e'
  on-primary-fixed-variant: '#38456c'
  secondary-fixed: '#baeddd'
  secondary-fixed-dim: '#9fd1c2'
  on-secondary-fixed: '#00201a'
  on-secondary-fixed-variant: '#1d4f44'
  tertiary-fixed: '#ffdf9d'
  tertiary-fixed-dim: '#f1bf48'
  on-tertiary-fixed: '#251a00'
  on-tertiary-fixed-variant: '#5b4300'
  background: '#faf9ff'
  on-background: '#181b23'
  surface-variant: '#e0e2ed'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.015em
  headline-sm:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: -0.01em
  title-md:
    fontFamily: Inter
    fontSize: 15px
    fontWeight: '500'
    lineHeight: 22px
    letterSpacing: -0.005em
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 26px
    letterSpacing: 0em
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 22px
    letterSpacing: 0em
  label-md:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '500'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.03em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1.25rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system establishes a focused, paper-first digital sanctuary for long-form study, research, and reading on native Android devices. Built upon Jetpack Compose and Material 3 principles, the visual language merges editorial restraint with refined digital utility. It eliminates chrome clutter, prioritizing document legibility and cognitive ease.

The aesthetic fuses **Warm Editorial Minimalism** with understated tactile precision. The interface recedes entirely when content is active, surfacing only contextually through whisper-soft borders, deliberate typographic hierarchy, and warm paper-inspired surfaces. The emotional signature is calm, scholarly, disciplined, and distinctly premium.

## Colors

The palette reproduces the tactile comfort of high-grade book paper balanced with archival inks.

### Light Mode Architecture
- **Primary Canvas (`#FAF9F6`)**: Natural off-white tone mimicking unbleached archival paper, preventing optical fatigue during extended sessions.
- **Surface Elevation (`#FFFFFF`)**: Pure white reserved for reading sheet backgrounds, floating modal sheets, and card surfaces.
- **Primary Ink & Brand (`#1D2B50`)**: Deep architectural navy serving as the foundational brand anchor, structural headers, and focused states.
- **Accent Indicator (`#E7B63F`)**: Warm gilding gold dedicated strictly to persistent state, reading progress tracks, active bookmark pins, and selection indicators.
- **Tertiary Accent (`#78A99B`)**: Muted sage applied to document status chips, completed reading badges, and non-disruptive tags.
- **Typography**: `#20232B` for high-contrast primary content; `#777B83` for archival metadata and page counts.
- **Boundaries (`#E7E5DF`)**: Low-contrast hairpins defining structural edges without breaking ocular flow.

### Dark Mode Architecture (Low-Lux Reading)
- **Primary Canvas (`#141821`)**: Abyssal slate calibrated to neutralize OLED glare while avoiding harsh true blacks.
- **Surface Elevation (`#1D2330`)**: Muted dark navy-slate for reading sheets and persistent bottom app bars.
- **Typography**: `#F2F1EC` for softened primary text legibility; `#A5A9B1` for secondary metadata.
- **Boundaries**: `#283042` hairline dividers.

## Typography

The typography relies on Inter’s neutral, highly legible glyph system configured to evoke Swiss print layouts.

- **Screen Headings (`headline-lg`)**: 24sp / 32sp line height with tight letter spacing for root library views, shelf titles, and system dialogs.
- **Section Headings (`headline-md`, `headline-sm`)**: 20sp and 16sp providing rigorous typographic rhythm between collections and document sections.
- **Document Titles (`title-md`)**: 15sp / 22sp line height with medium weight, tuned specifically to keep long PDF filenames readable across varying screen dimensions.
- **Reading Metadata (`label-md`, `label-sm`)**: 13sp and 11sp tracking page intervals, file dimensions, and last-read timestamps with slightly tracked letter spacing for immediate legibility at small scale.

## Layout & Spacing

Layout geometry follows an uncompromising 4dp / 8dp rhythmic baseline engineered natively for Jetpack Compose `Arrangement.spacedBy` and `PaddingValues`.

- **Mobile Viewport (< 600dp)**: Standard 16dp horizontal page margins with 16dp inter-column gutters. The library switches between a 2-column grid and a disciplined single-column document list.
- **Tablet & Foldable (>= 600dp)**: Dynamic 24dp to 32dp page margins accommodating multi-pane reading layouts (table of contents drawer docked alongside an edge-to-edge canvas).
- **Chrome Suppression**: When in active reading mode, chrome bars automatically retract to provide a seamless edge-to-edge layout, utilizing `WindowInsets.systemBars` padding only when overlays are toggled via single-tap gestures.

## Elevation & Depth

In alignment with an editorial design philosophy, visual hierarchy avoids heavy, saturated drop shadows. Depth is achieved via **Tonal Stratification and Low-Contrast Outlines**.

- **Level 0 (Base Canvas)**: Flat `#FAF9F6` background with no elevation.
- **Level 1 (Card & Shelf Surfaces)**: Pure `#FFFFFF` surface enclosed by a 1dp hairline border in `#E7E5DF`. Ambient shadow is restrained: `Y: 2dp, Blur: 8dp, Color: rgba(29, 43, 80, 0.04)`.
- **Level 2 (Modals, Context Menus & Scrim Sheets)**: `#FFFFFF` surface framed by the hairline boundary, backed by a softer depth shadow: `Y: 6dp, Blur: 20dp, Color: rgba(29, 43, 80, 0.08)`.
- **Active Scrims**: 40% `#141821` wash behind bottom sheets to maintain legibility without abrupt visual shock.

## Shapes

The geometric silhouette references architectural stationeries. UI boundaries strike a balance between fluid touch ergonomics and structured editorial precision.

- **Primary Cards**: Corner radii standardized at 16dp (`RoundedCornerShape(16.dp)`), softening thumbnail representations of formal paper sheets.
- **Floating Toolbars & Scrim Sheets**: 18dp top radii for bottom sheets; 12dp radii for floating contextual text-selection bars.
- **Micro-Elements (Chips, Badges)**: 8dp to full pill radius for touch targets and inline metadata tags.

## Components

### Buttons
- **Primary Action**: Solid `#1D2B50` container with `#FFFFFF` text, `RoundedCornerShape(12.dp)`, 48dp height minimum for touch compliance. No drop shadow; subtle 0.98x pressed-scale response.
- **Secondary / Ghost**: Transparent fill with a 1dp `#E7E5DF` border, `#1D2B50` text, highlighting to `#FAF9F6` on interaction.
- **Floating Reader Actions**: Circular 44dp containers using Level 2 elevation with `#1D2B50` icon tinting.

### Chips & Metadata Badges
- **Status Chips**: Muted Sage background (`#78A99B` at 15% opacity) paired with `#43665D` text.
- **Progress Badges**: Outlined container featuring a mini circular track in `#E7B63F` alongside 11sp metadata text.

### Document Cards (Grid & List)
- **Cover Presentation**: 16dp rounded card with a 1dp inner border. A simulated paper crease overlay (gradient from `rgba(0,0,0,0.04)` to transparent across 8dp) lines the left edge of the document thumbnail.
- **List Rows**: 72dp height, borderless with an `#E7E5DF` divider inset by 16dp from the left, highlighting the document title, reading percentage, and last-read date.

### Reading Progress Indicator
- Custom horizontal track: 3dp line thickness in `#E7E5DF` with the active buffer rendered in Warm Gold (`#E7B63F`), capped with a 6dp rounded pin for scrub feedback.

### Form Inputs & Search Fields
- 48dp height with `#FAF9F6` internal fill, 1dp `#E7E5DF` border, and 12dp corners. Focus transitions smoothly to a 1.5dp `#1D2B50` stroke with zero elevation shift.

### Selection & Annotation Controls
- Inline floating bar floating 16dp above selection with `#1D2330` container (inverted in light mode for intense contrast), `#FFFFFF` icons, and 8dp internal spacing.