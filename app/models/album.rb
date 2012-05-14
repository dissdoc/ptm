class Album < ActiveRecord::Base
  belongs_to :user

  has_many :photos, :dependent => :destroy
  has_many :photo_album_joins, :dependent => :destroy

  validates :name, :user_id, :presence => true

  def show_thumbnail
    unless collection
      photos.where("id = ?", thumbnail).first
    else
      photo_album_joins.where("photo_id = ?", thumbnail).first.photo
    end
  end
end
