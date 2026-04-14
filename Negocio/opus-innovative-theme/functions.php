<?php
/**
 * OPUS Innovative — functions.php
 *
 * @package opus-innovative
 */

// ── Theme setup ──
function opus_setup() {
    add_theme_support( 'title-tag' );
    add_theme_support( 'post-thumbnails' );
    add_theme_support( 'html5', array( 'search-form', 'comment-form', 'gallery', 'caption' ) );

    register_nav_menus( array(
        'primary' => __( 'Menu Principal', 'opus-innovative' ),
    ) );
}
add_action( 'after_setup_theme', 'opus_setup' );

// ── Enqueue styles & scripts ──
function opus_enqueue_assets() {
    // Google Fonts
    wp_enqueue_style(
        'opus-google-fonts',
        'https://fonts.googleapis.com/css2?family=DM+Sans:wght@300;400;500;700&family=Outfit:wght@300;400;500;600;700;800;900&display=swap',
        array(),
        null
    );

    // Theme stylesheet
    wp_enqueue_style(
        'opus-style',
        get_stylesheet_uri(),
        array( 'opus-google-fonts' ),
        wp_get_theme()->get( 'Version' )
    );

    // Main JS
    wp_enqueue_script(
        'opus-main',
        get_template_directory_uri() . '/js/main.js',
        array(),
        wp_get_theme()->get( 'Version' ),
        true
    );
}
add_action( 'wp_enqueue_scripts', 'opus_enqueue_assets' );
