package com.example.project30;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class TransitionManager {
    private void efeitoBorboleta(Parent node, double duracao) {
        // Configuração inicial - a borboleta começa invisível, pequena e girada
        node.setOpacity(0);
        node.setScaleX(0.3);
        node.setScaleY(0.3);
        node.setRotate(-20);
        node.setTranslateX(-100);
        node.setTranslateY(-50);

        // Animação de rotação suave (efeito de asas batendo - primeira parte)
        RotateTransition rotate1 = new RotateTransition(Duration.millis(duracao * 0.3), node);
        rotate1.setToAngle(10);
        rotate1.setCycleCount(2);
        rotate1.setAutoReverse(true);

        // Animação de rotação suave (efeito de asas batendo - segunda parte)
        RotateTransition rotate2 = new RotateTransition(Duration.millis(duracao * 0.2), node);
        rotate2.setToAngle(-5);
        rotate2.setCycleCount(2);
        rotate2.setAutoReverse(true);

        // Animação de escala (borboleta crescendo até o tamanho normal)
        ScaleTransition scale = new ScaleTransition(Duration.millis(duracao), node);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_OUT);

        // Animação de fade (borboleta aparecendo gradualmente)
        FadeTransition fade = new FadeTransition(Duration.millis(duracao * 0.7), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        // Animação de movimento (borboleta voando até a posição final)
        TranslateTransition fly = new TranslateTransition(Duration.millis(duracao), node);
        fly.setFromX(-100);
        fly.setFromY(-50);
        fly.setToX(0);
        fly.setToY(0);
        fly.setInterpolator(Interpolator.EASE_BOTH);

        // Criando a sequência de animações
        SequentialTransition borboleta = new SequentialTransition(
                new PauseTransition(Duration.millis(100)),
                fade,
                fly,
                new ParallelTransition(scale, rotate1),
                rotate2
        );

        // Inicia a animação
        borboleta.play();
    }
    // Animação BUTTERFLY (borboleta voando) - VERSÃO SIMPLES
    private static void butterflyAnimation(Node node, double duracao) {
        // Configuração inicial
        node.setOpacity(0);
        node.setScaleX(0.3);
        node.setScaleY(0.3);
        node.setRotate(-20);
        node.setTranslateX(-100);
        node.setTranslateY(-50);

        // Cria animações paralelas
        ParallelTransition animacao = new ParallelTransition();

        // Voa para posição
        TranslateTransition voar = new TranslateTransition(Duration.millis(duracao), node);
        voar.setToX(0);
        voar.setToY(0);

        // Cresce
        ScaleTransition crescer = new ScaleTransition(Duration.millis(duracao), node);
        crescer.setToX(1);
        crescer.setToY(1);

        // Aparece
        FadeTransition fade = new FadeTransition(Duration.millis(duracao), node);
        fade.setToValue(1);

        // Gira
        RotateTransition girar = new RotateTransition(Duration.millis(duracao), node);
        girar.setToAngle(0);

        // Adiciona tudo
        animacao.getChildren().addAll(voar, crescer, fade, girar);

        // Executa
        animacao.play();
    }
    // Tipos de transição disponíveis
    public enum TransitionType {
        SLIDE_RIGHT,    // Slide da direita
        SLIDE_LEFT,     // Slide da esquerda
        SLIDE_TOP,      // Slide de cima
        SLIDE_BOTTOM,   // Slide de baixo
        FADE,           // Fade simples
        ZOOM_IN,        // Zoom entrando
        ZOOM_OUT,       // Zoom saindo
        BOUNCE,         // Efeito bounce
        FLIP,           // Efeito flip
        MORPH           // Efeito morph combinado
    }

    /**
     * Função única para trocar telas com efeito
     * @param container O Pane onde está o conteúdo (ex: conteudoDinamico)
     * @param novaTela O novo conteúdo a ser carregado
     * @param tipo O tipo de transição desejada
     * @param duracao Duração da animação em milissegundos
     */
    public static void trocarTela(Pane container, Parent novaTela, TransitionType tipo, double duracao) {
        // Configuração inicial da nova tela
        novaTela.setOpacity(0);
        novaTela.setScaleX(1);
        novaTela.setScaleY(1);
        novaTela.setRotate(0);

        switch (tipo) {
            case SLIDE_RIGHT:
                novaTela.setTranslateX(800);
                break;
            case SLIDE_LEFT:
                novaTela.setTranslateX(-800);
                break;
            case SLIDE_TOP:
                novaTela.setTranslateY(-600);
                break;
            case SLIDE_BOTTOM:
                novaTela.setTranslateY(600);
                break;
            case ZOOM_IN:
                novaTela.setScaleX(0.5);
                novaTela.setScaleY(0.5);
                break;
            case ZOOM_OUT:
                novaTela.setScaleX(1.5);
                novaTela.setScaleY(1.5);
                break;
            case FLIP:
                novaTela.setScaleX(0);
                break;
        }

        // Remove tela atual e adiciona a nova
        container.getChildren().clear();
        container.getChildren().add(novaTela);

        // Cria a animação baseada no tipo
        Timeline animacao = new Timeline();

        switch (tipo) {
            case SLIDE_RIGHT:
            case SLIDE_LEFT:
            case SLIDE_TOP:
            case SLIDE_BOTTOM:
                TranslateTransition slide = new TranslateTransition(Duration.millis(duracao), novaTela);
                slide.setToX(0);
                slide.setToY(0);
                slide.play();
                break;

            case FADE:
                FadeTransition fade = new FadeTransition(Duration.millis(duracao), novaTela);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
                break;

            case ZOOM_IN:
            case ZOOM_OUT:
                ScaleTransition zoom = new ScaleTransition(Duration.millis(duracao), novaTela);
                zoom.setToX(1);
                zoom.setToY(1);
                zoom.play();

                FadeTransition fadeZoom = new FadeTransition(Duration.millis(duracao), novaTela);
                fadeZoom.setFromValue(0);
                fadeZoom.setToValue(1);
                fadeZoom.play();
                break;

            case BOUNCE:
                bounceAnimation(novaTela, duracao);
                break;

            case FLIP:
                flipAnimation(novaTela, duracao);
                break;

            case MORPH:
                morphAnimation(novaTela, duracao);
                break;
        }
    }

    /**
     * Versão simplificada com valores padrão (SLIDE_RIGHT, 400ms)
     */
    public static void trocarTela(Pane container, Parent novaTela) {
        trocarTela(container, novaTela, TransitionType.SLIDE_RIGHT, 400);
    }

    // Animação de bounce (quica)
    private static void bounceAnimation(Node node, double duracao) {
        node.setScaleX(0.8);
        node.setScaleY(0.8);
        node.setOpacity(0);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(duracao * 0.6), node);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(duracao * 0.2), node);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        FadeTransition fade = new FadeTransition(Duration.millis(duracao * 0.6), node);
        fade.setToValue(1);

        SequentialTransition bounce = new SequentialTransition(node, fade, scaleUp, scaleDown);
        bounce.play();
    }

    // Animação de flip (virar)
    private static void flipAnimation(Node node, double duracao) {
        node.setScaleX(0);
        node.setOpacity(0);

        Timeline flip = new Timeline(
                new KeyFrame(Duration.millis(duracao * 0.5), e -> node.setScaleX(1)),
                new KeyFrame(Duration.millis(duracao), e -> node.setOpacity(1))
        );

        flip.play();
    }

    // Animação morph combinada (mais completa)
    private static void morphAnimation(Node node, double duracao) {
        node.setTranslateX(400);
        node.setScaleX(0.7);
        node.setScaleY(0.7);
        node.setOpacity(0);
        node.setRotate(-15);

        ParallelTransition morph = new ParallelTransition();

        TranslateTransition slide = new TranslateTransition(Duration.millis(duracao), node);
        slide.setToX(0);

        ScaleTransition zoom = new ScaleTransition(Duration.millis(duracao), node);
        zoom.setToX(1);
        zoom.setToY(1);

        FadeTransition fade = new FadeTransition(Duration.millis(duracao), node);
        fade.setToValue(1);

        RotateTransition rotate = new RotateTransition(Duration.millis(duracao), node);
        rotate.setToAngle(0);

        morph.getChildren().addAll(slide, zoom, fade, rotate);
        morph.play();
    }
}