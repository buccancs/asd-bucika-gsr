package com.topdon.module.thermal.ir.event

import com.topdon.lib.core.repository.GalleryRepository.DirType

/**
 * gallery
 */
data class GalleryDirChangeEvent(val dirType: DirType)