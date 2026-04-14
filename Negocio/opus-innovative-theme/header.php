<!DOCTYPE html>
<html <?php language_attributes(); ?>>
<head>
    <meta charset="<?php bloginfo( 'charset' ); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <?php wp_head(); ?>
</head>
<body <?php body_class(); ?>>
<?php wp_body_open(); ?>

<!-- ─── NAV ─── -->
<nav>
    <div class="container">
        <a href="<?php echo esc_url( home_url( '/' ) ); ?>" class="logo">OPUS<span>.</span></a>
        <?php
        wp_nav_menu( array(
            'theme_location' => 'primary',
            'container'      => false,
            'menu_class'     => 'nav-links',
            'fallback_cb'    => 'opus_fallback_menu',
        ) );
        ?>
        <div class="mobile-toggle" aria-label="Menu">
            <span></span><span></span><span></span>
        </div>
    </div>
</nav>

<?php
/**
 * Fallback menu when no WordPress menu is assigned.
 */
function opus_fallback_menu() { ?>
    <ul class="nav-links">
        <li><a href="#servicos">Serviços</a></li>
        <li><a href="#tecnologias">Tecnologias</a></li>
        <li><a href="#sobre">Sobre</a></li>
        <li><a href="#blog">Blog</a></li>
        <li><a href="#contato" class="nav-cta">Fale conosco</a></li>
    </ul>
<?php }
