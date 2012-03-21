class PhotosController < ApplicationController
  before_filter :authenticate_user!

  def new
    @album = Album.find(params[:album_id])
    @photo = @album.photos.new
    @photo.build_geo
  end

  def create
    @album = Album.find(params[:album_id])
    @photo = @album.photos.new(params[:photo])
    if @photo.save!
      redirect_to @album
    else
      redirect_to root_path
    end
  end

  def show
    @album = Album.find(params[:album_id])
    @photo = @album.photos.find(params[:id])
  end

  def edit
    @album = Album.find(params[:album_id])
    @photo = @album.photos.find(params[:id])
    @photo.geo
  end

  def update
    @album = Album.find(params[:album_id])
    @photo = @album.photos.find(params[:id])
    if @photo.update_attributes(params[:photo])
      redirect_to [@album, @photo]
    else
      render :action => "edit"
    end
  end
end