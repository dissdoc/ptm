class Album < ActiveRecord::Base
  belongs_to :user

  has_many :photos, :dependent => :destroy

  validates :name, :user_id, :presence => true

  def show_thumbnail
    photos.where("id = ?", thumbnail).first
  end
end
