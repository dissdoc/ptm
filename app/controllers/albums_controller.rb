class AlbumsController < ApplicationController
  before_filter :authenticate_user!, :except => [:show]
  before_filter :set_album, :only => [:destroy, :edit, :update, :set_title]

  def index
    @albums = current_user.albums.all
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
      redirect_to albums_path
    else
      render :action => :new
    end
  end

  def destroy
    @album = current_user.albums.find(params[:id])
    @album.destroy
    redirect_to albums_path
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

  def link_photo
    collection = current_user.albums.find(params[:album_id])
    @join = collection.photo_album_joins.new(:photo_id => params[:photo_id].to_i)
    if @join.save!
      redirect_to albums_path
    else
      redirect_to root_path
    end
  end

  protected

  def set_album
    @album ||= current_user.albums.find(params[:id])
    redirect_to root_path if @album.blank?
  end
end