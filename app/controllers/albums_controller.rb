class AlbumsController < ApplicationController
  before_filter :authenticate_user!, :except => [:index, :show]

  def index
    @albums = Album.all
  end

  def show
    @album = Album.find(params[:id])
  end

  def new
    @album = current_user.albums.new
  end

  def create
    @album = current_user.albums.new(params[:album])
    if @album.save!
      redirect_to my_albums_path
    else
      redirect_to albums_path
    end
  end

  def destroy
    @album = current_user.albums.find(params[:id])
    @album.destroy
    redirect_to my_albums_path
  end

  def my
    @albums = current_user.albums.all
  end

  def share
    @album = Album.find(params[:album_id])
    @album.photos.each { |photo| photo.share_photo.sharing! }
    redirect_to albums_path
  end

  def edit
    @album = current_user.albums.find(params[:id])
  end

  def update
    @album = current_user.albums.find(params[:id])
    if @album.update_attributes(params[:album])
      redirect_to albums_path
    else
      redirect_to edit_album_path(@album)
    end
  end

  def set_title
    @album = current_user.albums.find(params[:id])
    if @album.update_attributes(:thumbnail => params[:thumbnail])
      redirect_to @album
    else
      redirect_to albums_path
    end
  end
end