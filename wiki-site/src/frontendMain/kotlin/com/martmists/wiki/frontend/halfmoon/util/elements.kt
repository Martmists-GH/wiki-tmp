package com.martmists.wiki.frontend.halfmoon.util

import org.w3c.dom.*

external interface HmButton
abstract external class HmButtonElement : HTMLButtonElement, HmButton
abstract external class HmAButtonElement : HTMLAnchorElement, HmButton
abstract external class HmCardElement : HTMLDivElement
abstract external class HmContentElement : HTMLDivElement
abstract external class HmRowElement : HTMLDivElement
abstract external class HmImageElement : HTMLImageElement
abstract external class HmNavbarElement : HTMLElement
abstract external class HmNavbarNavigationElement : HTMLUListElement
abstract external class HmSidebarElement : HTMLDivElement
abstract external class HmTableElement : HTMLTableElement
abstract external class HmAlertElement : HTMLDivElement
external interface HmBadge
abstract external class HmBadgeElement : HTMLSpanElement, HmBadge
abstract external class HmLinkBadgeElement : HTMLAnchorElement, HmBadge
abstract external class HmBreadcrumbElement : HTMLUListElement
abstract external class HmCollapseElement : HTMLDetailsElement
abstract external class HmCollapseHeaderElement : HTMLElement
abstract external class HmDropdownElement : HTMLDivElement
abstract external class HmDropdownMenuElement : HTMLDivElement
abstract external class HmModalElement : HTMLDivElement
abstract external class HmPaginationElement : HTMLUListElement
abstract external class HmProgressElement : HTMLDivElement
abstract external class HmProgressBarElement : HTMLDivElement
abstract external class HmProgressGroupElement : HTMLDivElement
abstract external class HmFormElement : HTMLFormElement
