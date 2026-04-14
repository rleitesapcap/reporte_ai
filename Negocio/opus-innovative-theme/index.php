<?php
/**
 * Fallback template — redireciona para a home.
 *
 * @package opus-innovative
 */

get_header(); ?>

<section class="hero" style="min-height:60vh;display:flex;align-items:center;justify-content:center;">
    <div class="container" style="text-align:center;">
        <h1><?php the_title(); ?></h1>
        <div style="color:var(--text-secondary);margin-top:1rem;">
            <?php
            if ( have_posts() ) :
                while ( have_posts() ) : the_post();
                    the_content();
                endwhile;
            endif;
            ?>
        </div>
    </div>
</section>

<?php get_footer();
