package com.andresuryana.metembangbali.utils

import com.andresuryana.metembangbali.R

@Suppress("SpellCheckingInspection")
object CategoryConstants {

    // Main Category
    const val SEKAR_AGUNG = "SekarAgung"
    private const val SEKAR_ALIT = "SekarAlit"
    private const val SEKAR_MADYA = "SekarMadya"
    private const val SEKAR_RARE = "SekarRare"

    // Sub Category - Sekar Alit
    private const val PUPUH_ADRI = "PupuhAdri"
    private const val PUPUH_DANGDANG = "PupuhDangdang"
    private const val PUPUH_DEMUNG = "PupuhDemung"
    private const val PUPUH_DURMA = "PupuhDurma"
    private const val PUPUH_GAMBUH = "PupuhGambuh"
    private const val PUPUH_GINADA = "PupuhGinada"
    private const val PUPUH_GINANTI = "PupuhGinanti"
    private const val PUPUH_MASKUMAMBANG = "PupuhMaskumambang"
    private const val PUPUH_MEGATRUH = "PupuhMegatruh"
    private const val PUPUH_PANGKUR = "PupuhPangkur"
    private const val PUPUH_PUCUNG = "PupuhPucung"
    private const val PUPUH_SEMARANDANA = "PupuhSemarandana"
    private const val PUPUH_SINOM = "PupuhSinom"


    // Sub Category - Sekar Madya
    private const val KIDUNG_BHUTA_YADNYA = "KidungBhutaYadnya"
    private const val KIDUNG_DEWA_YADNYA = "KidungDewaYadnya"
    private const val KIDUNG_MANUSA_YADNYA = "KidungManusaYadnya"
    private const val KIDUNG_PITRA_YADNYA = "KidungPitraYadnya"
    private const val KIDUNG_RSI_YADNYA = "KidungRsiYadnya"

    // Sub Category - Sekar Rare
    private const val GENDING_JEJANGERAN = "GendingJejangeran"
    private const val GENDING_RARE = "GendingRare"
    private const val GENDING_SANGHYANG = "GendingSanghyang"

    // Category Images
    val IMAGES = mutableMapOf(
        /* Main Category */
        Pair(SEKAR_AGUNG, R.drawable.sekar_agung),
        Pair(SEKAR_ALIT, R.drawable.sekar_alit),
        Pair(SEKAR_MADYA, R.drawable.sekar_madya),
        Pair(SEKAR_RARE, R.drawable.sekar_rare),

        /* Sub Category - Sekar Alit */
        Pair(PUPUH_ADRI, R.drawable.pupuh_adri),
        Pair(PUPUH_DANGDANG, R.drawable.pupuh_dangdang),
        Pair(PUPUH_DEMUNG, R.drawable.pupuh_demung),
        Pair(PUPUH_DURMA, R.drawable.pupuh_durma),
        Pair(PUPUH_GAMBUH, R.drawable.pupuh_gambuh),
        Pair(PUPUH_GINADA, R.drawable.pupuh_ginada),
        Pair(PUPUH_GINANTI, R.drawable.pupuh_ginanti),
        Pair(PUPUH_MASKUMAMBANG, R.drawable.pupuh_maskumambang),
        Pair(PUPUH_MEGATRUH, R.drawable.pupuh_megatruh),
        Pair(PUPUH_PANGKUR, R.drawable.pupuh_pangkur),
        Pair(PUPUH_PUCUNG, R.drawable.pupuh_pucung),
        Pair(PUPUH_SEMARANDANA, R.drawable.pupuh_semarandana),
        Pair(PUPUH_SINOM, R.drawable.pupuh_sinom),


        /* Sub Category - Sekar Madya */
        Pair(KIDUNG_BHUTA_YADNYA, R.drawable.kidung_bhuta_yadnya),
        Pair(KIDUNG_DEWA_YADNYA, R.drawable.kidung_dewa_yadnya),
        Pair(KIDUNG_MANUSA_YADNYA, R.drawable.kidung_manusa_yadnya),
        Pair(KIDUNG_PITRA_YADNYA, R.drawable.kidung_pitra_yadnya),
        Pair(KIDUNG_RSI_YADNYA, R.drawable.kidung_rsi_yadnya),

        /* Sub Category - Sekar Rare */
        Pair(GENDING_JEJANGERAN, R.drawable.gending_jejangeran),
        Pair(GENDING_RARE, R.drawable.gending_rare),
        Pair(GENDING_SANGHYANG, R.drawable.gending_sanghyang)
    )
}