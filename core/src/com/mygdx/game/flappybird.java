package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class flappybird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;

	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;

	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 2;
	private float posicaoInicialVerticalPassaro=0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float espacoEntreCanos;
	private Random random;
	private int pontos=0;
	private int pontuacaoMaxima=0;
	private boolean passouCano=false;
	private int estadoJogo = 0;
	private float posicaHorizontalPassaro = 0;

	BitmapFont textPontuacao;
	BitmapFont textReiniciar;
	BitmapFont textMlehorPontuacao;

	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;

	Preferences preferences;


	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720;
	private final float VIRTUAL_HEIGTH = 1280;


	@Override
	public void create (){
		inicializarTexturas();
		inicializarObjetos();
	}

	@Override public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificandoEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

	private void inicializarTexturas(){

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
	}

	private void inicializarObjetos(){
		batch = new SpriteBatch();
		random = new Random();
	}

	larguraDispositivo = VIRTUAL_WIDTH;
	alturaDisposivo = VIRTUAL_HEIGHT;
	posi
}
